package com.devd.commonsystem.ui.calendar

data class CustomDatePickerDialogState(
    var selectedDate: Long = System.currentTimeMillis(),
    val isCanChangeDate: Boolean = true,
    var isShowDialog: Boolean = false,
    val onClickConfirm: (dateMillis: Long) -> Unit = {},
    val onClickCancel: () -> Unit = {}
)