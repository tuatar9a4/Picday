package com.devd.model.local

data class DiaryInfo(
    val diaryId: Long,
    val diaryBookId: Long,
    val content: String,
    val mood: Int?,
    val weather: Int?,
    val createdAt: Long,
    val updatedAt: Long,
    val imageUrlList: List<String> = emptyList(),
    val tagList: List<String> = emptyList()
)