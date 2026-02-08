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
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val diaryBookRepository: DiaryBookRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    val bookInfo = mutableStateOf<DiaryBookInfo?>(null)
    val diaryList = mutableStateOf<List<DiaryInfo>>(emptyList())
    val searchDate = mutableStateOf<ZonedDateTime>(Instant.now().atZone(ZoneId.systemDefault()))
    val messageDialog = mutableStateOf<Int?>(null)
    val isLoading = mutableStateOf(false)

    fun fetchMainDiaryBook() {
        viewModelScope.launch {
            isLoading.value = true
            val uuid = dataStoreRepository.getPreferData(DataStoreKey.UserUID)!!
            messageDialog.value = null
            diaryBookRepository.fetchMajorDiaryBook(uuid)?.let { diaryBook ->
                bookInfo.value = diaryBook
                fetchDairiesByDiaryBook(diaryBook.bookId)
            } ?: run {
                showMessageDialog(R.string.fail_fetch_diary_book)
            }
        }
    }

    private fun fetchDairiesByDiaryBook(diaryBookId: Long) {
        viewModelScope.launch {
            val (start, end) = searchDate.value.toLocalDate().getCurrentMonthRangeMillis()
            diaryList.value =
                diaryBookRepository.fetchMonthDairiesByDiaryBook(diaryBookId, start, end)
            isLoading.value = false
        }
    }

    fun dismissDialog() {
        messageDialog.value = null
    }

    fun showMessageDialog(@StringRes message: Int) {
        messageDialog.value = message
    }

    fun changeSearchMonth(changeTime: Long) {
        isLoading.value = true
        searchDate.value = Instant.ofEpochMilli(changeTime).atZone(ZoneId.systemDefault())
        bookInfo.value?.bookId?.let { fetchDairiesByDiaryBook(it) }
    }

}