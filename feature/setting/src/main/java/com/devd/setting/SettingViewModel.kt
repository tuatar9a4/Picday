package com.devd.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devd.commonsystem.utils.FontList
import com.devd.commonsystem.utils.getDateStr
import com.devd.data.repository.DiaryBookRepository
import com.devd.data.repository.OracleRepository
import com.devd.datastore.DataStoreKey
import com.devd.datastore.DataStoreKey.DiaryLockPassword
import com.devd.datastore.DataStoreRepository
import com.devd.firebase.fcm.FcmExtension
import com.devd.model.local.LocalSettingData
import com.devd.model.local.MessageData
import com.devd.setting.data.ItemType
import com.devd.setting.data.SettingItem
import com.devd.setting.data.SettingType
import com.devd.setting.data.settingList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class SettingUiState(
    val isLoading: Boolean = false,
    val settingData: List<SettingItem> = settingList,
    val messageDialog: MessageData? = null,
    val alarmDialogInfo: AlarmData = AlarmData(),
    val syncDate: Long = 0L,
)

data class AlarmData(
    val isShow: Boolean = false,
    val selectHour: Int? = null,
    val selectMin: Int? = null
)

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val diaryBookRepository: DiaryBookRepository,
    private val oracleRepository: OracleRepository
) : ViewModel() {

    private val _settingUiState = MutableStateFlow(SettingUiState())
    val settingUiState = _settingUiState.asStateFlow()

    var savedSettingData = LocalSettingData()

    init {
        viewModelScope.launch { initSettingData() }
    }

    private suspend fun initSettingData() {
        savedSettingData = dataStoreRepository.getLocalSettingData()
        val newList = settingList.toMutableList().mapNotNull {
            when (it.type) {
                SettingType.FONT_TYPE -> {
                    val selectItem = FontList.entries[savedSettingData.fontIndexInt]
                    it.copy(
                        settingType = ItemType.ActionValue(isArrow = true, value = selectItem.name)
                    )
                }

                SettingType.ALERT_TIME -> {
                    val savedTime = dataStoreRepository.getPreferData(DataStoreKey.SavedAlarmTime)
                    it.copy(
                        settingType = ItemType.ActionValue(
                            isArrow = true,
                            value = savedTime?.let { StringBuilder(it).insert(2, ":").toString() }
                                ?: "-"
                        )
                    )
                }

                SettingType.APP_VERSION -> {
                    it.copy(settingType = ItemType.Value(""))
                }

                SettingType.DIARY_LOCK -> it.copy(settingType = ItemType.Switch(isOn = savedSettingData.isLockDiary))
                SettingType.DELETE_DATA -> it.copy(settingType = ItemType.Action(isArrow = true))
                        SettingType.CLOUD_SYNC -> null
            }
        }
        _settingUiState.update {
            it.copy(settingData = newList)
        }
    }

    fun syncDiaryData() {
        viewModelScope.launch {
            _settingUiState.update { it.copy(isLoading = true) }
            val uuid = dataStoreRepository.getUserInfo()?.uuid ?: return@launch
            val syncResult = diaryBookRepository.fetchNotSyncDiaryBooks(uuid)
            val newList = setValueWithType(
                SettingType.CLOUD_SYNC,
                if (syncResult) System.currentTimeMillis().getDateStr() else "-",
            )
            _settingUiState.update {
                it.copy(
                    isLoading = false,
                    settingData = newList
                )
            }
        }
    }

    fun checkFcmToken() {
        viewModelScope.launch {
            _settingUiState.update { it.copy(isLoading = true) }
            val token = dataStoreRepository.getPreferData(DataStoreKey.FcmToken)
                ?.let { FcmExtension.getFcmToken() }
            token ?: return@launch _settingUiState.update {
                it.copy(
                    isLoading = false,
                    messageDialog = MessageData("failGetToken", messageStr = "비정상 토큰입니다.")
                )
            }
            val savedTime = dataStoreRepository.getPreferData(DataStoreKey.SavedAlarmTime)
            dataStoreRepository.setPreferData(DataStoreKey.FcmToken, token)
            _settingUiState.update {
                it.copy(
                    isLoading = false,
                    alarmDialogInfo = it.alarmDialogInfo.copy(
                        isShow = true,
                        selectHour = savedTime?.take(2)?.toIntOrNull(),
                        selectMin = savedTime?.takeLast(2)?.toIntOrNull()
                    )
                )
            }
        }
    }

    fun changeSettingData(type: SettingType, value: Int) {
        viewModelScope.launch {
            when (type) {
                SettingType.FONT_TYPE -> {
                    savedSettingData = savedSettingData.copy(fontIndex = value.toString())
                    dataStoreRepository.setLocalSettingData(savedSettingData)
                    _settingUiState.update {
                        it.copy(
                            isLoading = false,
                            settingData = setValueWithType(
                                SettingType.FONT_TYPE,
                                valueStr = FontList.entries[value].name
                            )
                        )
                    }
                }

                SettingType.ALERT_TIME -> Unit
                SettingType.DELETE_DATA -> Unit
                SettingType.CLOUD_SYNC -> Unit
                SettingType.DIARY_LOCK -> Unit
                SettingType.APP_VERSION -> Unit
            }
        }
    }

    fun registerScheduleUserPush(sendTime: String) {
        viewModelScope.launch {
            try {
                dismissAlarmDialog()
                _settingUiState.update { it.copy(isLoading = true) }
                val savedTime = dataStoreRepository.getPreferData(DataStoreKey.SavedAlarmTime)

                val fcmToken = dataStoreRepository.getPreferData(DataStoreKey.FcmToken)!!

                val sendLocalTime = LocalTime.of(
                    sendTime.take(2).toIntOrNull() ?: 0,
                    sendTime.takeLast(2).toIntOrNull() ?: 0
                )
                val zonedSendTime = ZonedDateTime.now(ZoneId.systemDefault()).with(sendLocalTime)
                val utcSendTime = zonedSendTime.withZoneSameInstant(ZoneId.of("UTC"))

                val savedTimeToUtc = savedTime?.let {
                    val savedTime = LocalTime.of(
                        it.take(2).toIntOrNull() ?: 0,
                        it.takeLast(2).toIntOrNull() ?: 0
                    )
                    val localZonedSavedTime =
                        ZonedDateTime.now(ZoneId.systemDefault()).with(savedTime)
                    localZonedSavedTime.withZoneSameInstant(ZoneId.of("UTC"))
                }

                val formatter = DateTimeFormatter.ofPattern("HHmm")

                val data: HashMap<String, Any?> = hashMapOf(
                    "fcmToken" to fcmToken,
                    "utcTimeBucket" to utcSendTime.format(formatter),
                    "previousBucket" to savedTimeToUtc?.format(formatter)
                )

                val result = FcmExtension.sendAlarmDataToFirebaseServer(data)
                dataStoreRepository.setPreferData(DataStoreKey.SavedAlarmTime, sendTime)
                _settingUiState.update {
                    it.copy(
                        isLoading = false,
                        settingData = setValueWithType(
                            SettingType.ALERT_TIME,
                            valueStr = StringBuilder(sendTime).insert(2, ":").toString()
                        )
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setLockPassword(lockPassword: String?) {
        viewModelScope.launch {
            if (lockPassword != null) {
                _settingUiState.update { it.copy(isLoading = true) }
                dataStoreRepository.setPreferData(DiaryLockPassword, lockPassword)
            } else {
                dataStoreRepository.clearPreferData(DiaryLockPassword)
            }
            dataStoreRepository.setLocalSettingData(savedSettingData.copy(isLockDiary = lockPassword != null))
            _settingUiState.update {
                it.copy(
                    isLoading = false,
                    settingData = setValueWithType(
                        SettingType.DIARY_LOCK,
                        isOn = lockPassword != null
                    )
                )
            }
        }
    }

    fun deleteAllDiaryData() {
        viewModelScope.launch {
            _settingUiState.update { it.copy(isLoading = true, messageDialog = null) }
            val userInfo = dataStoreRepository.getUserInfo()
            diaryBookRepository.fetchAllDiaryBooks(userInfo!!.uuid).forEach {
                val diaryListOfBook = diaryBookRepository.fetchAllDairiesByDiaryBook(it.bookId)
                val deleteBook = diaryBookRepository.deleteBookInfo(it.bookId)
                deleteBook?.bookImage?.let { bookImage ->
                    val removeList =
                        diaryListOfBook.map { bookInfo -> bookInfo.imageUrlList.first() } + listOf(
                            bookImage
                        )
                    oracleRepository.deleteBucketImage(fileNames = removeList)
                }
            }
            _settingUiState.update {
                it.copy(
                    isLoading = false,
                    messageDialog = MessageData(
                        messageType = "completeDelete",
                        messageStr = "삭제가 성공되었습니다. 앱을 재시작해주세요."
                    )
                )
            }
        }
    }

    fun requestDeleteData() {
        _settingUiState.update {
            it.copy(
                messageDialog = MessageData(
                    messageType = "deleteDiary",
                    messageStr = "정말 데이터를 삭제하시겠습니까?\n 삭제된 일기데이터는 복구가 불가능하며 일기장을 새로 생성해야합니다.\n삭제 후 앱이 종료 됩니다."
                )
            )
        }
    }

    fun showMessageDialog(messageId: String) {
        _settingUiState.update {
            it.copy(
                messageDialog = MessageData(
                    "needPermission",
                    messageStr = messageId
                )
            )
        }
    }

    fun dismissAlarmDialog() {
        _settingUiState.update { it.copy(alarmDialogInfo = it.alarmDialogInfo.copy(isShow = false)) }
    }

    fun dismissMessageDialog() {
        _settingUiState.update { it.copy(messageDialog = null) }
    }


    private fun setValueWithType(
        type: SettingType,
        valueStr: String? = null,
        valueId: Int? = null,
        isOn: Boolean? = null,
    ): List<SettingItem> {
        val newList = settingUiState.value.settingData.map { item ->
            if (item.type == type) {
                return@map when (val currentType = item.settingType) {
                    is ItemType.Action -> item
                    is ItemType.ActionValue -> item.copy(
                        settingType = currentType.copy(value = valueStr, valueId = valueId)
                    )

                    is ItemType.Value -> item
                    is ItemType.Switch -> item.copy(
                        settingType = currentType.copy(isOn = isOn ?: false)
                    )
                }
            } else {
                item
            }
        }
        return newList
    }

}