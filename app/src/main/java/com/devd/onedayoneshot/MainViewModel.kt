package com.devd.onedayoneshot

import androidx.lifecycle.ViewModel
import com.devd.datastore.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    val dataChangeFlow = dataStoreRepository.userInfo

}