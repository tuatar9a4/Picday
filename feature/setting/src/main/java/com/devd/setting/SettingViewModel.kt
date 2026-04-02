package com.devd.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devd.commonsystem.utils.getDateStr
import com.devd.data.repository.DiaryBookRepository
import com.devd.datastore.DataStoreRepository
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

    init {
        viewModelScope.launch { fetchSavedData() }
    }

    private suspend fun fetchSavedData() {
        val settingData = dataStoreRepository.getLocalSettingData()
        val newList = setValueWithType(
            SettingType.CLOUD_SYNC,
            if (settingData.syncTime == 0L) "-" else settingData.syncTime.getDateStr(),
        )
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