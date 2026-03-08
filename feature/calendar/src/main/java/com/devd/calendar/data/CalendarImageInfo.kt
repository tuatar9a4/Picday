package com.devd.calendar.data

import androidx.compose.runtime.Composable
import com.devd.commonsystem.utils.rememberImageUrl

data class CalendarImageInfo(
    val day: Int,
    val isToday: Boolean,
    val isCurrentMonth: Boolean,
    val isSunDay: Boolean,
    val diaryId: Long?,
    val imageStr: String?,
    val contents: String?,
    val tagList: List<String>?,
) {
    @Composable
    fun imageUrl() = imageStr?.rememberImageUrl()
}