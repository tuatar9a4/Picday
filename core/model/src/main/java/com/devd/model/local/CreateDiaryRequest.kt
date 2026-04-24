package com.devd.model.local

data class CreateDiaryRequest(
    val bookId: Long,
    val content: String,
    val imageUrls: List<String>,
    val tags: List<String>,
    val createDate: Long,
    val updateDate: Long,
    val mood : Int
)

data class UpdateDiaryRequest(
    val diaryId: Long,
    val content: String,
    val imageUrls: List<String>,
    val tags: List<String>,
    val mood : Int
)
