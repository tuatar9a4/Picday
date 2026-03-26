package com.devd.calendar

import androidx.annotation.FloatRange
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.devd.calendar.data.CalendarImageInfo
import com.devd.calendar.navigation.CustomCalendarRoute
import com.devd.commonsystem.utils.getCurrentMonthRangeMillis
import com.devd.commonsystem.utils.getFirstDayMillis
import com.devd.data.repository.DiaryBookRepository
import com.devd.datastore.DataStoreRepository
import com.devd.model.local.DiaryBookInfo
import com.devd.model.local.DiaryInfo
import com.devd.model.local.DiaryPhaseType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
    val writeDisplayType: DiaryPhaseType = DiaryPhaseType.MOON,
    var bookName: String = "",
    var bookList: List<DiaryBookInfo> = emptyList(),
    @param:FloatRange(from = 0.0, to = 1.0) val writePercent: Float = 0f,
    val monthToList: Map<YearMonth, List<CalendarImageInfo>> = hashMapOf(),
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val diaryRepository: DiaryBookRepository,
    private val dataStoreRepository: DataStoreRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val route = savedStateHandle.toRoute<CustomCalendarRoute>()
    var bookId = route.selectBookID
    private val _calendarUiState =
        MutableStateFlow(CalendarUiState(selectDate = route.selectMillis))
    val calendarUiState = _calendarUiState.asStateFlow()

    private val _optionBookList = MutableStateFlow<List<DiaryBookInfo>?>(null)
    val optionBookList = _optionBookList.asStateFlow()


    init {
        viewModelScope.launch {
            val bookInfo = diaryRepository.fetchBookInfo(bookId)
            bookInfo?.let { bookInfo ->
                _calendarUiState.update {
                    it.copy(
                        writeDisplayType = bookInfo.bookPhaseType,
                        bookName = bookInfo.title
                    )
                }
            }
        }
    }

    fun fetchBookList() {
        viewModelScope.launch {
            if (calendarUiState.value.bookList.isNotEmpty()) {
                _optionBookList.emit(calendarUiState.value.bookList)
            } else {
                val uuid = dataStoreRepository.getUserInfo()?.uuid!!
                val bookList = diaryRepository.fetchAllDiaryBooks(uuid)
                _calendarUiState.update { it.copy(bookList = bookList) }
                _optionBookList.emit(calendarUiState.value.bookList)
            }
        }
    }

    fun dismissBookList() {
        viewModelScope.launch {
            _optionBookList.emit(null)
        }
    }

    fun fetchNewBookDiaryImage(title: String) {
        viewModelScope.launch {
            dismissBookList()
            _calendarUiState.update { it.copy(bookName = title, monthToList = hashMapOf()) }
            fetchDiaryImageWithMonth(
                millis = calendarUiState.value.selectDate,
                isPreMove = false,
                isNextMove = false
            )
        }
    }

    fun fetchDiaryImageWithMonth(millis: Long, isPreMove: Boolean, isNextMove: Boolean) {
        viewModelScope.launch {
            //이번달 데이터 확인 후 삽입
            val currentYM = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
                .toLocalDate().let { YearMonth.from(it) }
            if (!isPreMove && !isNextMove && calendarUiState.value.monthToList.isNotEmpty()) return@launch
            _calendarUiState.update { it.copy(isLoading = true, selectDate = millis) }

            val newList = calendarUiState.value.monthToList.toMutableMap()

            if (!calendarUiState.value.monthToList.contains(currentYM)) {
                val currentDeferred = async { fetchMonthData(millis) }
                val diaryList = currentDeferred.await()
                val writeCount = diaryList.filter { it.isCurrentMonth && it.diaryId != null }.size
                val dayCount = currentYM.lengthOfMonth().toFloat()
                val currentMonthDayCount = writeCount / dayCount
                _calendarUiState.update { it.copy(writePercent = currentMonthDayCount) }
                newList[currentYM] = currentDeferred.await()
            } else {
                val writeCount =
                    newList[currentYM]!!.filter { it.isCurrentMonth && it.diaryId != null }.size
                val dayCount = currentYM.lengthOfMonth().toFloat()
                val currentMonthDayCount = writeCount / dayCount
                _calendarUiState.update { it.copy(writePercent = currentMonthDayCount) }
            }

            //이전달 데이터 확인 후 삽입
            val prevYM = currentYM.minusMonths(1)
            val prevMillis = prevYM.getFirstDayMillis()
            if (!calendarUiState.value.monthToList.contains(prevYM)) {
                val prevDeferred = async { fetchMonthData(prevMillis) }
                newList[prevYM] = prevDeferred.await()
            }

            //다음달 데이터 확인 후 삽입
            val nextYM = currentYM.plusMonths(1)
            val nextMillis = nextYM.getFirstDayMillis()
            if (!calendarUiState.value.monthToList.contains(nextYM)) {
                val nextDeferred = async { fetchMonthData(nextMillis) }
                newList[nextYM] = nextDeferred.await()
            }

            _calendarUiState.update { state ->
                state.copy(isLoading = false, monthToList = newList)
            }
        }
    }

    private suspend fun fetchMonthData(millis: Long): List<CalendarImageInfo> {
        val (start, end) = millis.getCurrentMonthRangeMillis(true)
        val diaryList = diaryRepository.fetchMonthDairiesByDiaryBook(bookId, start, end)
        return createCalendarImageInfos(millis, diaryList)
    }

    private fun createCalendarImageInfos(
        millis: Long,
        diaryList: List<DiaryInfo>
    ): List<CalendarImageInfo> {
        val selectMonth = Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate().withDayOfMonth(1)

        val firstDayOfWeekValue = selectMonth.dayOfWeek.value % 7
        val calendarStartDay = selectMonth.minusDays(firstDayOfWeekValue.toLong())
        val weekCount = ceil((firstDayOfWeekValue + selectMonth.lengthOfMonth()) / 7f).toInt()

        val today = LocalDate.now()
        val currentYearMonth = YearMonth.from(selectMonth)

        return (0..<weekCount * 7).map {
            val selectDay = calendarStartDay.plusDays(it.toLong())
            val diaryWithDay = diaryList.find { diary -> diary.localDataWithCreate == selectDay }

            CalendarImageInfo(
                day = selectDay.dayOfMonth,
                isToday = selectDay == today,
                isCurrentMonth = YearMonth.from(selectDay) == currentYearMonth,
                isSunDay = selectDay.dayOfWeek == DayOfWeek.SUNDAY,
                diaryId = diaryWithDay?.diaryId,
                imageStr = diaryWithDay?.imageUrlList?.firstOrNull(),
                contents = diaryWithDay?.content,
                tagList = diaryWithDay?.tagList
            )
        }
    }

}