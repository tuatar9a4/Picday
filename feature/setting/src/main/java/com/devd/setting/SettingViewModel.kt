package com.devd.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devd.commonsystem.utils.FontList
import com.devd.commonsystem.utils.getDateStr
import com.devd.data.repository.DiaryBookRepository
import com.devd.datastore.DataStoreRepository
import com.devd.model.local.LocalSettingData
import com.devd.setting.data.ItemType
import com.devd.setting.data.SettingItem
import com.devd.setting.data.SettingType
import com.devd.setting.data.settingList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingUiState(
    val isLoading: Boolean = false,
    val settingData: List<SettingItem> = settingList,
    val syncDate: Long = 0L,
)

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val diaryBookRepository: DiaryBookRepository
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
                    it.copy(settingType = ItemType.ActionValue(isArrow = true, value = "시간"))
                }

                SettingType.APP_VERSION -> {
                    it.copy(settingType = ItemType.Value(""))
                }

                SettingType.CLOUD_SYNC -> null
                SettingType.MONTH_TYPE -> null
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

    fun changeSettingData(type: SettingType, value: Int) {
        viewModelScope.launch {
            when (type) {
                SettingType.FONT_TYPE -> {
                    savedSettingData = savedSettingData.copy(fontIndex = value.toString())
                    dataStoreRepository.setLocalSettingData(savedSettingData)
                }

                SettingType.CLOUD_SYNC -> Unit
                SettingType.ALERT_TIME -> TODO()
                SettingType.MONTH_TYPE -> Unit
                SettingType.APP_VERSION -> TODO()
            }
        }
    }


    private fun setValueWithType(
        type: SettingType,
        valueStr: String? = null,
        valueId: Int? = null
    ): List<SettingItem> {
        val newList = settingUiState.value.settingData.map { item ->
            if (item.type == type) {
                val currentType = item.settingType
                return@map when (currentType) {
                    is ItemType.Action -> item
                    is ItemType.ActionValue -> item.copy(
                        settingType = currentType.copy(value = valueStr, valueId = valueId)
                    )

                    is ItemType.Value -> item
                }
            } else {
                item
            }
        }
        return newList
    }

}