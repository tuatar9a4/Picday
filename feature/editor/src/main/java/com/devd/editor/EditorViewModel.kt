package com.devd.editor

import androidx.lifecycle.ViewModel
import com.devd.commonsystem.ui.calendar.CustomDatePickerDialogState
import com.devd.data.repository.OracleRepository
import com.devd.datastore.DataStoreKey
import com.devd.datastore.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val oracleRepository: OracleRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    var editorText = ""

    private val _customDatePickerDialogState = MutableStateFlow(CustomDatePickerDialogState())
    val customDatePickerDialogState: StateFlow<CustomDatePickerDialogState> get() = _customDatePickerDialogState.asStateFlow()


    fun initSelectDate(initDate: Long) {
        _customDatePickerDialogState.update {
            it.copy(
                selectedDate = initDate,
                onClickConfirm = ::changeSelectData,
                onClickCancel = ::dismissDatePickerDialog
            )
        }
    }

    fun changeSelectData(dateMillis: Long) {
        _customDatePickerDialogState.update {
            it.copy(isShowDialog = false, selectedDate = dateMillis)
        }
    }

    fun showDatePickerDialog() {
        _customDatePickerDialogState.update { it.copy(isShowDialog = true) }
    }

    fun dismissDatePickerDialog() {
        _customDatePickerDialogState.update { it.copy(isShowDialog = false) }
    }

    fun setDiaryText(text: String) {
        editorText = text
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