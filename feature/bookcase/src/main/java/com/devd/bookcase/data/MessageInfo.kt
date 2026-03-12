package com.devd.bookcase.data

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

const val NONE = "NONE"
const val FAIL_UPDATE_BOOK = "failUpdateBook"
const val SUCCESS_UPDATE_BOOK = "successUpdateBook"


data class MessageInfo @SuppressLint("SupportAnnotationUsage") constructor(
    val type: String,
    @param:StringRes val messageId: Int? = null,
    @param:StringRes val messageStr: String? = null
) {
    @Composable
    fun getMessage() =
        if (type == NONE) null else messageId?.let { stringResource(it) } ?: messageStr ?: ""

}
