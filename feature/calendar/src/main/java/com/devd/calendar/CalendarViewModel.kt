package com.devd.calendar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.devd.calendar.data.CalendarImageInfo
import com.devd.calendar.navigation.CustomCalendarRoute
import com.devd.commonsystem.utils.getCurrentMonthRangeMillis
import com.devd.data.repository.DiaryBookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject
import kotlin.math.ceil

data class CalendarUiState(
    val isLoading: Boolean = false,
    val selectDate: Long = System.currentTimeMillis(),
    val imageList: List<CalendarImageInfo> = emptyList()
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val diaryRepository: DiaryBookRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val route = savedStateHandle.toRoute<CustomCalendarRoute>()
    private val bookId = route.selectBookID
    private val _calendarUiState =
        MutableStateFlow(CalendarUiState(selectDate = route.selectMillis))
    val calendarUiState = _calendarUiState.asStateFlow()

    init {
        _calendarUiState.update { it.copy(isLoading = true) }
        fetchDiaryImageWithMonth(bookId, route.selectMillis)
    }

    fun fetchDiaryImageWithMonth(bookId: Long, millis: Long) {
        viewModelScope.launch {
            val (start, end) = millis.getCurrentMonthRangeMillis(true)

            val diaryList =
                diaryRepository.fetchMonthDairiesByDiaryBook(bookId, start, end)

            val selectMonth = Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .withDayOfMonth(1)
                .toLocalDate()

            val firstDayOfWeekValue = selectMonth.dayOfWeek.value % 7
            val calendarStartDay = selectMonth.minusDays(firstDayOfWeekValue.toLong())

            val weekCount = ceil((firstDayOfWeekValue + selectMonth.lengthOfMonth()) / 7f).toInt()
            val calendarImageInfos = mutableListOf<CalendarImageInfo>()
            (0..<weekCount * 7).forEach {
                val selectDay = calendarStartDay.plusDays(it.toLong())
                val diaryWithDay =
                    diaryList.find { diary -> diary.localDataWithCreate == selectDay }
                val isToday = selectDay == LocalDate.now()
                val isCurrentMonth = YearMonth.from(selectDay) == YearMonth.now()
                val isSunDay = selectDay.dayOfWeek == DayOfWeek.SUNDAY

                val imageInfo = CalendarImageInfo(
                    day = selectDay.dayOfMonth,
                    isToday = isToday,
                    isCurrentMonth = isCurrentMonth,
                    isSunDay = isSunDay,
                    diaryId = diaryWithDay?.diaryId,
                    imageStr = diaryWithDay?.imageUrlList?.firstOrNull(),
                    contents = diaryWithDay?.content,
                    tagList = diaryWithDay?.tagList
                )

                calendarImageInfos.add(imageInfo)
            }
            Timber.d("Check=> ${calendarImageInfos}")
            _calendarUiState.update { it.copy(isLoading = false, imageList = calendarImageInfos) }

        }

    }

}