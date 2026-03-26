package com.devd.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devd.data.repository.DiaryBookRepository
import com.devd.datastore.DataStoreRepository
import com.devd.intro.data.IntroUiState
import com.devd.intro.data.MoveToHome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val diaryBookRepository: DiaryBookRepository
) : ViewModel() {

    val isLoading = MutableStateFlow(false)
    private val _introUiState = MutableSharedFlow<IntroUiState>()
    val introUiState get() = _introUiState.asSharedFlow()

    init {
        viewModelScope.launch {
            isLoading.emit(true)
            delay(3000)
            if (existsUid()) {
                changeLoadingState(false)
                _introUiState.emit(MoveToHome)
            } else {
                isLoading.emit(false)
            }
        }
    }

    suspend fun changeLoadingState(state: Boolean) {
        isLoading.emit(state)
    }


    suspend fun fetchSavedNickName(): Boolean {
        val savedUUID = dataStoreRepository.getUserInfo()?.uuid ?: return false
        return diaryBookRepository.hasDiaryBook(savedUUID)
    }


    suspend fun existsUid() = dataStoreRepository.getUserInfo()?.uuid != null

}