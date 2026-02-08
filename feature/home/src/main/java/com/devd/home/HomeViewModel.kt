package com.devd.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devd.data.repository.DiaryBookRepository
import com.devd.datastore.DataStoreKey
import com.devd.datastore.DataStoreRepository
import com.devd.model.local.DiaryBookInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val diaryBookRepository: DiaryBookRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    val bookInfo = mutableStateOf<DiaryBookInfo?>(null)
    val diaryList = mutableStateOf<DiaryBookInfo?>(null)

    fun fetchMainDiaryBook() {
        viewModelScope.launch {
            val uuid = dataStoreRepository.getPreferData(DataStoreKey.UserUID)!!
            bookInfo.value = diaryBookRepository.fetchMajorDiaryBook(uuid)!!
        }
    }


}