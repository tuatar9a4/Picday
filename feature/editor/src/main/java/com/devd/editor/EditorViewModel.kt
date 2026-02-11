package com.devd.editor

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.devd.commonsystem.ui.calendar.CustomDatePickerDialogState
import com.devd.data.repository.OracleRepository
import com.devd.datastore.DataStoreKey
import com.devd.datastore.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val oracleRepository: OracleRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    var editorText = ""

    val customDatePickerDialogState = mutableStateOf<CustomDatePickerDialogState?>(null)

    fun initSelectDate(initDate: Long) {
        customDatePickerDialogState.value = CustomDatePickerDialogState(
            selectedDate = initDate,
            onClickConfirm = { dateMillis ->
                customDatePickerDialogState.value = customDatePickerDialogState.value?.copy(
                    isShowDialog = false,
                    selectedDate = dateMillis
                )
            },
            onClickCancel = {
                customDatePickerDialogState.value = customDatePickerDialogState.value?.copy(
                    isShowDialog = false,
                )
            }
        )
    }

    fun setDiaryText(text: String) {
        editorText = text
    }

    fun showDatePickerDialog() {
        customDatePickerDialogState.value = customDatePickerDialogState.value?.copy(
            isShowDialog = true
        )
    }

    suspend fun uploadImageToBuket(fileUrl: File) {
        val userUUID = dataStoreRepository.getPreferData(DataStoreKey.UserUID) ?: return

        oracleRepository.uploadImageFile(
            header = userUUID,
            file = fileUrl
        ).collect {
            Timber.d("Check UploadState => $it")
        }
    }
}