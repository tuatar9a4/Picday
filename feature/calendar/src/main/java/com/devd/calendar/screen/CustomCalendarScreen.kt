package com.devd.calendar.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.size.Size
import com.devd.calendar.data.CalendarImageInfo
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.Black33Color
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.BlackD9Color
import com.devd.commonsystem.theme.BlackF4Color
import com.devd.commonsystem.theme.RedColor
import com.devd.commonsystem.theme.TransParents
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.utils.getFirstDayMillis
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.math.ceil

@SuppressLint("ConfigurationScreenWidthHeight")
@Preview
@Composable
fun CustomCalendarScreen(
    selectDate: Long = System.currentTimeMillis(),
    infoList: Map<YearMonth, List<CalendarImageInfo>> = emptyMap(),
    onDateSelector: (CalendarImageInfo) -> Unit = {},
    onChangeDate: (selectMillis: Long, isPre: Boolean, isNext: Boolean) -> Unit = { _, _, _ -> }
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

    val currentMonth = remember(pagerState.targetPage) {
        val isPreMove = pagerState.targetPage < pagerState.settledPage
        val isNextMove = pagerState.targetPage > pagerState.settledPage
        val newMonth =
            anchorMonth.plusMonths((pagerState.targetPage - (Int.MAX_VALUE / 2)).toLong())
        onChangeDate(newMonth.getFirstDayMillis(), isPreMove, isNextMove)

        newMonth
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
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
                imageList = infoList[pageMonth],
                onDateSelector = onDateSelector
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
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${currentMonth.year}.${currentMonth.monthValue}",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 18.sp,
                color = BlackColor
            ),
        )
        Image(
            painter = painterResource(R.drawable.icon_drop_down),
            colorFilter = ColorFilter.tint(BlackColor),
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
enum class CalendarState { Collapsed, Expanded }
@Composable
fun CalendarGrid(
    currentMonth: YearMonth,
    imageList: List<CalendarImageInfo>?,
    onDateSelector: (info: CalendarImageInfo) -> Unit
) {
    // 이달 초로 이동
    val firstDayOfMonth = currentMonth.atDay(1)
    // 시작 주
    val firstDayOfWeekValue = firstDayOfMonth.dayOfWeek.value % 7
    // 몇 주 인지
    val weekCount = ceil((firstDayOfWeekValue + currentMonth.lengthOfMonth()) / 7f).toInt()

    val calendarStartDay = firstDayOfMonth.minusDays(firstDayOfWeekValue.toLong())

    val calendarItemCount = imageList?.size ?: (7 * weekCount)


    val deviceWidthPx = LocalWindowInfo.current.containerDpSize.width
    val density = LocalDensity.current
    val boxWidth = with(density) { (deviceWidthPx / 7) }
    val imagePixel = with(density) { boxWidth.toPx().toInt() }

    val anchors = DraggableAnchors {
        CalendarState.Collapsed at 0f // 축소 상태 (1:1)
        CalendarState.Expanded at 300f // 확장 상태 (9:16) - 실제 드래그 거리(px)
    }

    val state = remember {
        AnchoredDraggableState(
            initialValue = CalendarState.Expanded,
            anchors = anchors
        )
    }


    val dragProgress by remember {
        derivedStateOf {
            val fullRange = 300f // Expanded 앵커에 설정한 px 값
            val currentOffset = state.offset.coerceIn(0f, fullRange)
            currentOffset / fullRange
        }
    }

    val currentAspectRatio = lerp(1f, 9 / 16f, dragProgress)
    val extraTextAlpha = 1f - dragProgress // 축소될 때(0.0) 보이고 확장될 때(1.0) 사라짐
    val imageAlpha = dragProgress // 확장될 때 보임

    SideEffect {
        state.updateAnchors(
            DraggableAnchors {
                CalendarState.Collapsed at 0f
                CalendarState.Expanded at 300f
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth()
            .anchoredDraggable(
                state = state,
                orientation = Orientation.Vertical,
                enabled = true,

            )
    ) {
        (0..<calendarItemCount).toList().chunked(7).forEach { week ->
            Row() {
                week.forEach { day ->
                    val dayInfo = imageList?.getOrNull(day)
                    val date = calendarStartDay.plusDays((day).toLong())
                    val isToday = dayInfo?.isToday ?: (date == LocalDate.now())
                    val isFuture = (date > LocalDate.now())
                    val isCurrentMonth =
                        dayInfo?.isCurrentMonth ?: (YearMonth.from(date) == currentMonth)
                    val imageUrl = dayInfo?.imageUrl()
                    Box(
                        modifier = Modifier
                            .width(boxWidth)
                            .aspectRatio(currentAspectRatio)
//                            .aspectRatio(9 / 16f)
                            .background(
                                color = if (isFuture) WhiteColor else BlackF4Color,
                            )
                            .clickable {
                                imageList?.getOrNull(day)?.diaryId?.let { onDateSelector(imageList[day]) }
                            },
                    ) {
                        imageUrl?.let {
                            AsyncImage(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(it)
                                    .size(Size(imagePixel, imagePixel))
                                    .build(),
                                alpha = if (isCurrentMonth) 1f else 0.4f,
                                contentDescription = null
                            )
                        }
                        Text(
                            modifier = Modifier
                                .padding(2.dp)
                                .align(Alignment.TopStart)
                                .background(
                                    color = if (isToday) Black33Color else TransParents,
                                    shape = CircleShape
                                )
                                .alpha(if(isFuture && !isCurrentMonth) 0.4f else 1f)
                                .padding(vertical = 3.dp, horizontal = 5.dp),
                            text = dayInfo?.day?.toString() ?: "${date.dayOfMonth}",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = if (isToday) WhiteColor
                                else if (isFuture && isCurrentMonth) Black33Color
                                else if (imageUrl != null && isCurrentMonth) WhiteColor
                                else if (imageUrl == null && isCurrentMonth) BlackD9Color
                                else BlackD9Color
                            )
                        )
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = BlackF4Color
                        )
                    }
                }

            }

        }
    }
}