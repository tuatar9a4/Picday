package com.devd.calendar.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.devd.calendar.data.CalendarImageInfo
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.AccentColor
import com.devd.commonsystem.theme.BlackOpacity40Color
import com.devd.commonsystem.theme.OneDayTypography
import com.devd.commonsystem.theme.RedColor
import com.devd.commonsystem.theme.WhiteColor
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@SuppressLint("ConfigurationScreenWidthHeight")
@Preview
@Composable
fun CustomCalendarScreen(
    selectDate: Long = System.currentTimeMillis(),
    infoList: List<CalendarImageInfo> = emptyList()
) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val anchorPage = Int.MAX_VALUE / 2
    val anchorMonth = remember { YearMonth.now() }

    val initialPage = remember(selectDate) {
        val selectedYearMonth = Instant.ofEpochMilli(selectDate)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .let { YearMonth.from(it) }

        val monthOffset = ChronoUnit.MONTHS.between(anchorMonth, selectedYearMonth).toInt()
        anchorPage + monthOffset
    }
    val pagerState = rememberPagerState(initialPage = initialPage) { Int.MAX_VALUE }
    val scope = rememberCoroutineScope()

    val currentMonth = remember(pagerState.currentPage) {
//        YearMonth.from(LocalDate.ofEpochDay(selectDate))
        anchorMonth.plusMonths((pagerState.currentPage - (Int.MAX_VALUE / 2)).toLong())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        CalendarHeader(
            currentMonth = currentMonth,
            onPreviousMonth = {
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            },
            onNextMonth = {
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        DaysOfWeekHeader()

        Spacer(modifier = Modifier.height(8.dp))

        // 스와이프 가능한 영역
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    (screenWidth - 32.dp) / 7 * 16 / 9 * 6
                ),
            verticalAlignment = Alignment.Top
        ) { page ->
            // 각 페이지(화면)에 해당하는 월을 계산해서 달력 그리드를 그립니다.
            val pageMonth = YearMonth.now().plusMonths((page - initialPage).toLong())

            CalendarGrid(
                currentMonth = pageMonth,
                imageList = infoList,
                onDateSelected = { },
            )
        }
    }
}

@Composable
fun CalendarHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.clickable(onClick = onPreviousMonth),
            painter = painterResource(id = R.drawable.icon_andgle_left),
            contentDescription = null
        )

        Text(
            text = "${currentMonth.year}년 ${currentMonth.monthValue}월",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Image(
            modifier = Modifier.clickable(onClick = onNextMonth),
            painter = painterResource(id = R.drawable.icon_angle_right),
            contentDescription = null
        )
    }
}

@Composable
fun DaysOfWeekHeader() {
    val daysOfWeek = listOf("일", "월", "화", "수", "목", "금", "토")
    Row(modifier = Modifier.fillMaxWidth()) {
        daysOfWeek.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = if (day == "일") RedColor else if (day == "토") Color.Blue else Color.Black
            )
        }
    }
}

@Composable
fun CalendarGrid(
    currentMonth: YearMonth,
    imageList: List<CalendarImageInfo>,
    onDateSelected: (LocalDate) -> Unit
) {
//    // 이달 초로 이동
//    val firstDayOfMonth = currentMonth.atDay(1)
//    // 시작 주
//    val firstDayOfWeekValue = firstDayOfMonth.dayOfWeek.value % 7
//    // 몇 주 인지
//    val weekCount = ceil((firstDayOfWeekValue + currentMonth.lengthOfMonth()) / 7f).toInt()
//
//    val calendarStartDay = firstDayOfMonth.minusDays(firstDayOfWeekValue.toLong())

    val deviceWidthPx = LocalWindowInfo.current.containerDpSize.width - 32.dp
    val density = LocalDensity.current
    val boxWidth = with(density) { (deviceWidthPx / 7) }

//    val days = (0..<(7 * weekCount)).toList()
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        imageList.chunked(7).forEach { week ->
            Row() {
                week.forEach { dayInfo ->
                    val isToday = dayInfo.isToday
                    val isCurrentMonth = dayInfo.isCurrentMonth
                    val isSunDay = dayInfo.isSunDay
//                    val date = calendarStartDay.plusDays((day).toLong())
//                    val isToday = date == LocalDate.now()
//                    val isCurrentMonth = YearMonth.from(date) == currentMonth
//                    val isSunDay = date.dayOfWeek == DayOfWeek.SUNDAY
                    Box(
                        modifier = Modifier
                            .width(boxWidth)
                            .aspectRatio(9 / 16f)
                            .padding(2.dp)
                            .background(
                                color = BlackOpacity40Color,
                                shape = RoundedCornerShape(5.dp)
                            )
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        dayInfo.imageUrl()?.let {
                            AsyncImage(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(5.dp)),
                                contentScale = ContentScale.Crop,
                                model = it,
                                contentDescription = null
                            )
                        }
                        Text(
                            modifier = Modifier
                                .padding(2.dp)
                                .align(Alignment.TopStart)
                                .alpha(if (isToday || isCurrentMonth) 1f else 0.4f)
                                .padding(vertical = 3.dp, horizontal = 5.dp),
                            text = dayInfo?.day?.toString() ?: "-",
                            style = OneDayTypography.labelLarge.copy(
                                color = if (isToday) AccentColor else if (isSunDay) RedColor else WhiteColor
                            )
                        )
                    }
                }

            }

        }
    }
}