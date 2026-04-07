package com.devd.model.local

data class DiaryBookInfo(
    val bookId: Long,
    val bookColor: Int = 0,
    val bookImage: String? = null,
    val title: String,
    val description: String? = null,
    val bookPhaseType: DiaryPhaseType = DiaryPhaseType.MOON,
    val createDate: Long = System.currentTimeMillis(),
    var monthWritePercent: Float = 0f,
    var isMajor: Boolean = false
)
