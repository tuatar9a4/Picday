package com.devd.model.local

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

data class MessageData(
    val messageType: String,
    val messageStr: String? = null,
    @param:StringRes val messageId: Int? = null,
) {
    @Composable
    fun getMessage() = messageId?.let { stringResource(it) } ?: messageStr ?: ""
}