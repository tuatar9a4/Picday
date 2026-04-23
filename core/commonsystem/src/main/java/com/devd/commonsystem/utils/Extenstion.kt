package com.devd.commonsystem.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalDensity
import androidx.core.content.FileProvider
import com.devd.commonsystem.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.ceil

object StringRexFormat {
    const val ID_REGEX = "^[a-zA-Z0-9]{2,10}$"
    const val ID_WORD_REGEX = "^[a-zA-Z0-9]+$"
    const val PASSWORD_WORD_REGEX = "^[A-Za-z\\d!@#$%^&*]+$"
    const val PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*]{8,20}$"
    const val NICKNAME_REGEX = "^[가-힣ㄱ-ㅎㅏ-ㅣa-zA-Z0-9 ]{0,10}$"
}

fun String.checkValidateRex(regexFormat: String): Boolean {
    return Regex(regexFormat).matches(this)
}


fun DayOfWeek.convertWeekStr(): Int {
    return when (this) {
        DayOfWeek.SUNDAY -> R.string.sunday_text
        DayOfWeek.MONDAY -> R.string.monday_text
        DayOfWeek.TUESDAY -> R.string.tuesday_text
        DayOfWeek.WEDNESDAY -> R.string.wednesday_text
        DayOfWeek.THURSDAY -> R.string.thursday_text
        DayOfWeek.FRIDAY -> R.string.friday_text
        DayOfWeek.SATURDAY -> R.string.saturday_text
    }
}

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}

/* 특정 날의 첫번째와 마지막 시간 */
fun LocalDate.getStartEndRangeMillis(): Pair<Long, Long> {
    val zoneId = ZoneId.systemDefault()
    val start = this.atStartOfDay(zoneId).toInstant()
    val end = this.atTime(LocalTime.MAX).atZone(zoneId).toInstant()

    return start.toEpochMilli() to end.toEpochMilli()
}

/* 해당 달의 첫번째날과 마지막날의 Millis */
fun Long.getCurrentMonthRangeMillis(includePreMonth: Boolean = false): Pair<Long, Long> {
    val zoneId = ZoneId.systemDefault()
    val dateTime = Instant.ofEpochMilli(this).atZone(zoneId)
    val currentMonth = YearMonth.from(dateTime)

    // 1. 달력 시작일 (첫 번째 칸)
    val firstDayOfMonth = currentMonth.atDay(1)
    val firstDayOfWeekValue = if (includePreMonth) firstDayOfMonth.dayOfWeek.value % 7 else 0
    val calendarStartDay = firstDayOfMonth.minusDays(firstDayOfWeekValue.toLong())

    // 2. 주 수 및 전체 칸 수 계산
    val weekCount = ceil((firstDayOfWeekValue + currentMonth.lengthOfMonth()) / 7f).toInt()
    val totalCells = weekCount * 7

    // 3. 달력 종료일 (마지막 칸)
    val calendarEndDay = calendarStartDay.plusDays((totalCells - 1).toLong())

    // 4. Millis 변환
    val startMillis = calendarStartDay.atStartOfDay(zoneId).toInstant().toEpochMilli()

    // 마지막 날의 23:59:59.999
    val endMillis = calendarEndDay.atTime(LocalTime.MAX).atZone(zoneId).toInstant().toEpochMilli()

    return Pair(startMillis, endMillis)
}


fun ZonedDateTime.isCurrentMonth(): Boolean {
    val targetMonth = YearMonth.from(this)
    val currentMonth = YearMonth.now(ZoneId.systemDefault())
    return targetMonth == currentMonth
}

fun YearMonth.getFirstDayMillis() =
    this.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()


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


fun Long.getDateStr(pattern: String = "yyyy-MM-dd"): String {
    val instant = Instant.ofEpochMilli(this)
    val formatter = DateTimeFormatter.ofPattern(pattern)
        .withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}

// --- 비트맵 저장 및 공유 유틸리티 함수 ---
suspend fun Context.shareBitmap( bitmap: Bitmap) {
    // a. 임시 파일 생성
    val cachePath = File(cacheDir, "images")
    cachePath.mkdirs() // 폴더가 없으면 생성
    val file = File(cachePath, "captured_image.png")

    // b. 비트맵을 파일로 쓰기
    withContext(Dispatchers.IO) {
        FileOutputStream(file).use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }
    }

    // c. FileProvider를 통해 URI 가져오기
    // *주의: AndroidManifest.xml에 FileProvider 설정이 필요합니다.*
    val contentUri: Uri = FileProvider.getUriForFile(
        this,
        "${packageName}.fileprovider", // Manifest의 authorities와 일치해야 함
        file
    )

    // d. 공유 Intent 생성 및 실행
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, contentUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // 읽기 권한 부여 필수
    }

    startActivity(Intent.createChooser(intent, "이미지 공유하기"))
}