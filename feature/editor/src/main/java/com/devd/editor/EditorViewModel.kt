package com.devd.editor

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.devd.commonsystem.ui.calendar.CustomDatePickerDialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(

) : ViewModel() {

    var editorText = ""

    val customDatePickerDialogState = mutableStateOf<CustomDatePickerDialogState?>(null)

    fun initSelectDate(initDate : Long) {
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

    fun showDatePickerDialog(){
        customDatePickerDialogState.value = customDatePickerDialogState.value?.copy(
            isShowDialog = true
        )
    }
}