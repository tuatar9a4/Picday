package com.devd.bookcase.data

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

const val NONE = "NONE"
const val FAIL_UPDATE_BOOK = "failUpdateBook"
const val SUCCESS_UPDATE_BOOK = "successUpdateBook"
const val FAIL_SAVE_BOOK = "failSaveBook"
const val SUCCESS_SAVE_BOOK = "successSaveBook"
const val SUCCESS_DELETE_BOOK = "successDeleteBook"
const val FAIL_DELETE_BOOK = "failDeleteBook"
const val NEED_BOOK_IMAGE = "needBookImage"


data class MessageInfo @SuppressLint("SupportAnnotationUsage") constructor(
    val type: String,
    @param:StringRes val messageId: Int? = null,
    @param:StringRes val messageStr: String? = null
) {
    @Composable
    fun getMessage() =
        if (type == NONE) null else messageId?.let { stringResource(it) } ?: messageStr ?: ""

}
