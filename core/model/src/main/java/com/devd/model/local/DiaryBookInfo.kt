package com.devd.model.local

data class DiaryBookInfo(
    val bookId: Long,
    val title: String,
    val description: String?,
    val bookPhaseType : DiaryPhaseType,
    var monthWritePercent: Float = 0f
)
