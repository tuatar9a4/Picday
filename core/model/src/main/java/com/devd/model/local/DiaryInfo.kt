package com.devd.model.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale

@Serializable
@Parcelize
data class DiaryInfo(
    val diaryId: Long,
    val diaryBookId: Long,
    val content: String,
    val mood: Int? = null,
    val weather: Int? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val imageUrlList: List<String> = emptyList(),
    val tagList: List<String> = emptyList()
) : Parcelable {
    val createDay: Int
        get() {
            return Instant.ofEpochMilli(createdAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate().dayOfMonth
        }
    val localDataWithCreate: LocalDate
        get() = Instant.ofEpochMilli(createdAt).atZone(ZoneId.systemDefault()).toLocalDate()

    val isTodayItem: Boolean
        get() {
            val targetDate = Instant.ofEpochMilli(createdAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            val today = LocalDate.now(ZoneId.systemDefault())

            return targetDate == today
        }

    fun getDateStr(format: String): String {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        val date = Date(createdAt)
        return dateFormat.format(date)
    }
}