package com.devd.editor.data

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

const val ASK_SAVE = "askSave"
const val SAVE_SUCCESS = "saveSuccess"
const val SAVE_FAIL = "saveFail"

data class MessageInfo(
    val type: String,
    @param:StringRes val messageId: Int? = null,
    @param:StringRes val messageStr: String? = null
) {
    @Composable
    fun getMessage() = messageId?.let { stringResource(it) } ?: messageStr ?: ""

}
