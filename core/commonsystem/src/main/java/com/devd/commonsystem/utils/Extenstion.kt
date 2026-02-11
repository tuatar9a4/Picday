package com.devd.commonsystem.utils

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalDensity
import com.devd.commonsystem.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters
import java.util.Calendar

object StringRexFormat {
    const val ID_REGEX = "^[a-zA-Z0-9]{2,10}$"
    const val ID_WORD_REGEX = "^[a-zA-Z0-9]+$"
    const val PASSWORD_WORD_REGEX = "^[A-Za-z\\d!@#$%^&*]+$"
    const val PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*]{8,20}$"
    const val NICKNAME_REGEX = "^[가-힣a-zA-Z0-9]{0,10}$"
}

fun String.checkValidateRex(regexFormat: String): Boolean {
    return Regex(regexFormat).matches(this)
}


fun Int.convertWeekStr(): Int {
    return when (this) {
        Calendar.SUNDAY -> R.string.sunday_text
        Calendar.MONDAY -> R.string.monday_text
        Calendar.TUESDAY -> R.string.tuesday_text
        Calendar.WEDNESDAY -> R.string.wednesday_text
        Calendar.THURSDAY -> R.string.thursday_text
        Calendar.FRIDAY -> R.string.friday_text
        else -> R.string.saturday_text
    }
}

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}

fun LocalDate.getCurrentMonthRangeMillis(): Pair<Long, Long> {
    val zoneId = ZoneId.systemDefault()
    val start = this.withDayOfMonth(1)
        .atStartOfDay(zoneId)
        .toInstant()

    val end = this.with(TemporalAdjusters.lastDayOfMonth())
        .atTime(LocalTime.MAX)
        .atZone(zoneId)
        .toInstant()

    return start.toEpochMilli() to end.toEpochMilli()
}

fun ZonedDateTime.isCurrentMonth(): Boolean {
    val targetMonth = YearMonth.from(this)
    val currentMonth = YearMonth.now(ZoneId.systemDefault())

    return targetMonth == currentMonth
}


fun LazyListState.centerItemIndex(): Int? {
    val layoutInfo = layoutInfo
    val visibleItems = layoutInfo.visibleItemsInfo
    if (visibleItems.isEmpty()) return null

    val viewportCenter = layoutInfo.viewportStartOffset +
            layoutInfo.viewportEndOffset / 2

    return visibleItems.minByOrNull { item ->
        val itemCenter = item.offset + item.size / 2
        kotlin.math.abs(itemCenter - viewportCenter)
    }?.index
}