package com.devd.model.local

data class CreateDiaryRequest(
    val bookId: Long,
    val content: String,
    val imageUrls: List<String>,
    val tags: List<String>
)
