package com.devd.editor.data

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

const val NONE = "NONE"
const val ASK_SAVE = "askSave"
const val SAVE_SUCCESS = "saveSuccess"
const val SAVE_UPDATE = "saveUpdate"
const val SAVE_FAIL = "saveFail"
const val FAIL_LOAD_DIARY = "failLoadDiary"

data class MessageInfo @SuppressLint("SupportAnnotationUsage") constructor(
    val type: String,
    @param:StringRes val messageId: Int? = null,
    @param:StringRes val messageStr: String? = null
) {
    @Composable
    fun getMessage() =
        if (type == NONE) null else messageId?.let { stringResource(it) } ?: messageStr ?: ""

}
