package com.devd.user.register

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devd.data.repository.DiaryBookRepository
import com.devd.data.repository.UserRepository
import com.devd.data.utils.CallResult
import com.devd.datastore.DataStoreKey
import com.devd.datastore.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val diaryBookRepository: DiaryBookRepository
) : ViewModel() {

    val id = mutableStateOf("")
    val isCheckDuplicate = mutableStateOf(false)
    val password = mutableStateOf("")
    val nickname = mutableStateOf("")
    val diaryName = mutableStateOf("")

    private val _uiState = MutableSharedFlow<RegisterUIState>()
    val uiState get() = _uiState.asSharedFlow()


    suspend fun checkValidateId(id: String) {
        isCheckDuplicate.value = userRepository.checkExistsId(id)
    }

    fun requestMakeId() {
        viewModelScope.launch {
            Timber.d("Finish : ${id.value} ${password.value} ${nickname.value} ${diaryName.value}")
            dataStoreRepository.setPreferData(DataStoreKey.UserNickName, nickname.value)
            diaryBookRepository.insertNewDiaryBook(diaryName.value, "").run {
                when (this) {
                    is CallResult.NetworkError -> {
                        _uiState.emit(RegisterUIState.FailMakeDiary(this.message))
                    }

                    is CallResult.Success -> {
                        Timber.d("CheckDiaryList => ${diaryBookRepository.fetchAllDairies()}")
                        _uiState.emit(RegisterUIState.SuccessMakeId)
                    }
                }
            }
        }
    }
}

sealed interface RegisterUIState {
    data object SuccessMakeId : RegisterUIState
    data class FailMakeDiary(val message: String) : RegisterUIState
}