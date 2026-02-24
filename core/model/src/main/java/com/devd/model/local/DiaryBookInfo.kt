package com.devd.model.local

data class DiaryBookInfo(
    val bookId: Long,
    val bookImage: String? = null,
    val title: String,
    val description: String?,
    val bookPhaseType: DiaryPhaseType,
    val createDate: Long,
    var monthWritePercent: Float = 0f
)
