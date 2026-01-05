package com.devd.intro

import androidx.lifecycle.ViewModel
import com.devd.datastore.DataStoreKey
import com.devd.datastore.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {


    suspend fun fetchSavedNickName() :String? {
        return dataStoreRepository.getPreferData(DataStoreKey.UserNickName)
    }

}