package com.devd.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devd.data.repository.DiaryBookRepository
import com.devd.datastore.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val diaryBookRepository: DiaryBookRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {


    fun fetchMainDiaryBook(){
        viewModelScope.launch {
//            val uuid = dataStoreRepository.getPreferData(DataStoreKey.UserUID)
//            diaryBookRepository.fetchAllDairies(uuid)
        }
    }


}