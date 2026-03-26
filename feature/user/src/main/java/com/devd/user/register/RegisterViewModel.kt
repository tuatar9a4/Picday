package com.devd.user.register

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devd.data.repository.DiaryBookRepository
import com.devd.data.repository.OracleRepository
import com.devd.data.repository.UserRepository
import com.devd.data.utils.CallResult
import com.devd.datastore.DataStoreKey
import com.devd.datastore.DataStoreRepository
import com.devd.model.local.DiaryBookInfo
import com.devd.model.local.DiaryPhaseType
import com.devd.model.local.FailUpload
import com.devd.model.local.StartUpload
import com.devd.model.local.SuccessUpload
import com.devd.model.local.Uploading
import com.devd.model.remote.SignupResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val diaryBookRepository: DiaryBookRepository,
    private val oracleRepository: OracleRepository,
) : ViewModel() {

    val id = mutableStateOf("")
    val isCheckDuplicate = mutableStateOf(false)
    val password = mutableStateOf("")
    val nickname = mutableStateOf("")

    private val _uiState = MutableSharedFlow<RegisterUIState>()
    val uiState get() = _uiState.asSharedFlow()

    private val _simpleMessage = MutableSharedFlow<SimpleMessageState?>()
    val simpleMessage get() = _simpleMessage.asSharedFlow()

    private val _diaryBookDialog = MutableStateFlow(DiaryBookDialog())
    val diaryBookDialog get() = _diaryBookDialog.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading get() = _isLoading.asStateFlow()

    private var userInfo: SignupResponse? = null

    suspend fun checkValidateId(id: String) {
        _isLoading.emit(true)
        val result = userRepository.checkExistsId(id)
        isCheckDuplicate.value = result
        if (!result) {
            _simpleMessage.emit(SimpleMessageState("중복된 아이디입니다."))
        }
        _isLoading.emit(false)
    }

    fun showDiaryBookDialog() {
        _diaryBookDialog.update { it.copy(isShow = true) }
    }

    fun dismissBookDialog() {
        _diaryBookDialog.update { it.copy(isShow = false) }
    }

    private suspend fun requestNewUserId(): Boolean {
        _isLoading.emit(true)
        userInfo = userRepository.registerNewId(
            email = id.value,
            password = password.value,
            nickname = nickname.value,
        )
        userInfo?.let {
            val loginInfo =
                userRepository.requestLoginUser(id.value, password.value) ?: return false
            dataStoreRepository.setPreferData(DataStoreKey.UserToken, loginInfo.accessToken)
            dataStoreRepository.setPreferData(DataStoreKey.UserReToken, loginInfo.refreshToken)
            dataStoreRepository.setUserInfo(it.toUserInfo())
        }
        return userInfo != null
    }

    fun saveAndMakeBookInfo(
        imageFile: File?, title: String, description: String, monthType: DiaryPhaseType
    ) {
        viewModelScope.launch {
            if (!validateDiaryInfo(imageFile, title)) return@launch
            if (requestNewUserId()) {
                val newUserId = userInfo?.uuid!!
                var uploadImagePath: String? = null
                if (imageFile != null) {
                    when (val result =
                        oracleRepository.uploadImageFile(newUserId, imageFile).last()) {
                        is SuccessUpload -> uploadImagePath = "$newUserId/${result.uploadFileName}"
                        is FailUpload -> return@launch _simpleMessage.emit(
                            SimpleMessageState(result.errorMessage)
                        )

                        is Uploading, StartUpload -> Unit
                    }
                }

                _diaryBookDialog.update {
                    it.copy(
                        isShow = false,
                        bookInfo = it.bookInfo.copy(
                            bookImage = uploadImagePath,
                            title = title,
                            description = description,
                            bookPhaseType = monthType
                        )
                    )
                }
                insertDiaryBook(newUserId)
            } else {
                _isLoading.emit(false)
            }
        }
    }

    private suspend fun insertDiaryBook(userUUID: String) {
        val bookInfo = diaryBookDialog.value.bookInfo
        diaryBookRepository.insertNewDiaryBook(
            uuid = userUUID,
            bookImage = bookInfo.bookImage!!,
            bookTitle = bookInfo.title.trim(),
            bookDescription = bookInfo.description!!
        ).run {
            when (this) {
                is CallResult.NetworkError -> {
                    _isLoading.emit(false)
                    _simpleMessage.emit(SimpleMessageState(this.message))
                }

                is CallResult.Success -> {
                    _isLoading.emit(false)
                    _uiState.emit(RegisterUIState.SuccessMakeId)
                }
            }
        }
    }

    private suspend fun validateDiaryInfo(bookImage: File?, title: String): Boolean {
        if (nickname.value.length < 2 || nickname.value.isBlank()) {
            _simpleMessage.emit(SimpleMessageState("Nickname 은 글자 2자 이상!"))
            return false
        }
        if (title.length < 2 || title.isBlank()) {
            _simpleMessage.emit(SimpleMessageState("BookName 은 글자 2자 이상!"))
            return false
        }

        if (bookImage == null) {
            _simpleMessage.emit(SimpleMessageState("Book 대표 이미지를 선택해주세요"))
            return false
        }
        return true
    }

    fun clearMessage() {
        viewModelScope.launch { _simpleMessage.emit(null) }
    }

}

sealed interface RegisterUIState {
    data object SuccessMakeId : RegisterUIState
}

data class SimpleMessageState(val message: String)

data class DiaryBookDialog(
    val isShow: Boolean = false,
    val bookInfo: DiaryBookInfo = DiaryBookInfo(
        bookId = -1,
        title = "First Diary",
        description = "My First Diary",
        bookPhaseType = DiaryPhaseType.MOON,
        createDate = System.currentTimeMillis(),
        monthWritePercent = 0f,
    )
)
