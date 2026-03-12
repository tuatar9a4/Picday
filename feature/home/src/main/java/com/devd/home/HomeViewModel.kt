package com.devd.home

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devd.commonsystem.R
import com.devd.commonsystem.utils.getCurrentMonthRangeMillis
import com.devd.commonsystem.utils.isCurrentMonth
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
    var isShowCalendar: Boolean = false,
    var isShowImagePicker: Boolean = false,
    val isShowBookDialog: Boolean = false,
    var uriForCrop: Uri? = null,
    var bookInfo: DiaryBookInfo? = null,
    var diaryList: List<DiaryInfo> = emptyList(),
    var searchDate: ZonedDateTime = Instant.now().atZone(ZoneId.systemDefault()),
    @param:StringRes var dialogMessage: Int? = null
) {
    @Composable
    fun getDialogMessage() = dialogMessage?.let { stringResource(it) }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val diaryBookRepository: DiaryBookRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> get() = _homeUiState.asStateFlow()

    var storedUUID : String? = null

    fun fetchMainDiaryBook() {
        viewModelScope.launch {
            _homeUiState.update { it.copy(isLoading = true) }
            storedUUID = dataStoreRepository.getPreferData(DataStoreKey.UserUID)!!
            diaryBookRepository.fetchMajorDiaryBook(storedUUID!!)?.let { diaryBook ->
                fetchDairiesByDiaryBook(diaryBook)
            } ?: run {
                showMessageDialog(R.string.fail_fetch_diary_book)
            }
        }
    }

    private fun fetchDairiesByDiaryBook(diaryBook: DiaryBookInfo) {
        viewModelScope.launch {
            val (start, end) = homeUiState.value.searchDate.toInstant().toEpochMilli()
                .getCurrentMonthRangeMillis()
            val monthDiaries =
                diaryBookRepository.fetchMonthDairiesByDiaryBook(diaryBook.bookId, start, end)
                    .toMutableList()
            val isCurrentMonth = homeUiState.value.searchDate.isCurrentMonth()
            if (monthDiaries.isEmpty() || (!monthDiaries.first().isTodayItem && isCurrentMonth)) {
                monthDiaries.add(
                    0,
                    DiaryInfo(
                        diaryId = -1,
                        diaryBookId = -1,
                        content = "",
                        createdAt = -1,
                        updatedAt = -1
                    )
                )
            }
            _homeUiState.update {
                it.copy(
                    isLoading = false,
                    bookInfo = diaryBook,
                    diaryList = monthDiaries
                )
            }
        }
    }

    suspend fun fetchTodayDiary() =
        diaryBookRepository.fetchOneDiaryForDate(
            homeUiState.value.bookInfo?.bookId!!,
            System.currentTimeMillis()
        )


    fun showCalendarDialog() {
        _homeUiState.update { it.copy(isShowCalendar = true) }
    }

    fun dismissCalendar() {
        _homeUiState.update { it.copy(isShowCalendar = false) }
    }

    fun showMessageDialog(@StringRes message: Int) {
        _homeUiState.update { it.copy(dialogMessage = message) }
    }

    fun dismissMessageDialog() {
        _homeUiState.update { it.copy(dialogMessage = null) }
    }

    fun showImagePickerDialog() {
        _homeUiState.update { it.copy(isShowImagePicker = true) }
    }

    fun dismissImagePickerDialog() {
        _homeUiState.update { it.copy(isShowImagePicker = false) }
    }

    fun showBookDialog() {
        _homeUiState.update { it.copy(isShowBookDialog = (it.bookInfo != null)) }
    }

    fun dismissBookDialog() {
        _homeUiState.update { it.copy(isShowBookDialog = false) }

    }

    fun changeCropImageDialog(uri: Uri?) {
        _homeUiState.update { it.copy(uriForCrop = uri) }
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