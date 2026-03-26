package com.devd.user.register.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

enum class MessageType {
    LOGIN_FAIL
}

data class MessageInfo(
    val type: MessageType,
    val message: String? = null,
    val messageId: Int? = null
) {
    val messageStr
        @Composable
        get() = message ?: messageId?.let { stringResource(it) }
}
