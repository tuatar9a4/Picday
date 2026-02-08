package com.devd.model.local

data class DiaryBookInfo(
    val bookId: Long,
    val title: String,
    val description: String?,
    var monthWritePercent: Float = 0f
)
