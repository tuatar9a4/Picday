package com.devd.home

import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devd.commonsystem.R
import com.devd.data.repository.DiaryBookRepository
import com.devd.datastore.DataStoreKey
import com.devd.datastore.DataStoreRepository
import com.devd.model.local.DiaryBookInfo
import com.devd.model.local.DiaryInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val diaryBookRepository: DiaryBookRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    val bookInfo = mutableStateOf<DiaryBookInfo?>(null)
    val diaryList = mutableStateOf<List<DiaryInfo>>(emptyList())
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
            diaryList.value = diaryBookRepository.fetchDairiesByDiaryBook(diaryBookId)
            isLoading.value = false
        }
    }

    fun dismissDialog() {
        messageDialog.value = null
    }

    fun showMessageDialog(@StringRes message: Int) {
        messageDialog.value = message
    }

}