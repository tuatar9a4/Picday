package com.devd.model.local

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

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
) {
    val createDay: Int
        get() {
            val date = Calendar.getInstance().clone() as Calendar
            date.timeInMillis = createdAt
            return date.get(Calendar.DAY_OF_MONTH)
        }

    val isTodayItem : Boolean
        get() {
            val targetDate = Instant.ofEpochMilli(createdAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            val today = LocalDate.now(ZoneId.systemDefault())

            return targetDate == today
        }
}