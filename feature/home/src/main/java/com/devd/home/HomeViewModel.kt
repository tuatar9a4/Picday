package com.devd.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devd.data.repository.DiaryBookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val diaryBookRepository: DiaryBookRepository,
) : ViewModel() {


    fun fetchMainDiaryBook(){
        viewModelScope.launch {
//            diaryBookRepository.fetchAllDairies()
        }
    }


}