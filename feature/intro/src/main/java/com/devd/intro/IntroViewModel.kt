package com.devd.intro

import androidx.lifecycle.ViewModel
import com.devd.data.repository.DiaryBookRepository
import com.devd.datastore.DataStoreKey
import com.devd.datastore.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val diaryBookRepository: DiaryBookRepository
) : ViewModel() {


    suspend fun fetchSavedNickName() :Boolean {
        val savedUUID = dataStoreRepository.getPreferData(DataStoreKey.UserUID)?:return false
        return diaryBookRepository.hasDiaryBook(savedUUID)
    }

}