package com.devd.home

import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devd.commonsystem.R
import com.devd.commonsystem.utils.getCurrentMonthRangeMillis
import com.devd.data.repository.DiaryBookRepository
import com.devd.datastore.DataStoreKey
import com.devd.datastore.DataStoreRepository
import com.devd.model.local.DiaryBookInfo
import com.devd.model.local.DiaryInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

data class HomeUiState(
    var isLoading: Boolean = false,
    var isShowCalendar : Boolean = false,
    var bookInfo: DiaryBookInfo? = null,
    var diaryList: List<DiaryInfo> = emptyList(),
    var searchDate: ZonedDateTime = Instant.now().atZone(ZoneId.systemDefault()),
    @param:StringRes var dialogMessage: Int? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val diaryBookRepository: DiaryBookRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> get() = _homeUiState.asStateFlow()

    val messageDialog = mutableStateOf<Int?>(null)
    val isLoading = mutableStateOf(false)

    fun fetchMainDiaryBook() {
        viewModelScope.launch {
            _homeUiState.update { it.copy(isLoading = true) }
            val uuid = dataStoreRepository.getPreferData(DataStoreKey.UserUID)!!
            diaryBookRepository.fetchMajorDiaryBook(uuid)?.let { diaryBook ->
                fetchDairiesByDiaryBook(diaryBook)
            } ?: run {
                showMessageDialog(R.string.fail_fetch_diary_book)
            }
        }
    }

    private fun fetchDairiesByDiaryBook(diaryBook: DiaryBookInfo) {
        viewModelScope.launch {
            val (start, end) = homeUiState.value.searchDate.toLocalDate()
                .getCurrentMonthRangeMillis()
            val monthDiaries =
                diaryBookRepository.fetchMonthDairiesByDiaryBook(diaryBook.bookId, start, end)
            _homeUiState.update {
                it.copy(
                    isLoading = false,
                    bookInfo = diaryBook,
                    diaryList = monthDiaries
                )
            }
        }
    }

    fun showCalendarDialog(){
        _homeUiState.update { it.copy(isShowCalendar = true) }
    }

    fun dismissCalendar() {
        _homeUiState.update { it.copy(isShowCalendar = false) }
    }

    fun dismissDialog() {
        _homeUiState.update { it.copy(dialogMessage = null) }
    }

    fun showMessageDialog(@StringRes message: Int) {
        _homeUiState.update { it.copy(dialogMessage = message) }
    }

    fun changeSearchMonth(changeTime: Long) {
        _homeUiState.update {
            it.copy(
                isLoading = true,
                isShowCalendar = false,
                searchDate = Instant.ofEpochMilli(changeTime).atZone(ZoneId.systemDefault())
            )
        }
        homeUiState.value.bookInfo?.let { fetchDairiesByDiaryBook(it) }
    }

}