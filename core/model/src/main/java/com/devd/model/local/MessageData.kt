package com.devd.model.local

data class MessageData(
    val messageType: String,
    val messageStr: String? = null,
    val messageId: String? = null,
)