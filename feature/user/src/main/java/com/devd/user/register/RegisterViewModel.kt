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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.util.UUID
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


    suspend fun checkValidateId(id: String) {
        isCheckDuplicate.value = userRepository.checkExistsId(id)
    }

    suspend fun requestMakeId(): String {
        Timber.d("Finish : ${id.value} ${password.value} ${nickname.value}")
        val userUUID =
            dataStoreRepository.getPreferData(DataStoreKey.UserUID) ?: UUID.randomUUID().toString()

        dataStoreRepository.setPreferData(DataStoreKey.UserNickName, nickname.value.trim())
        dataStoreRepository.setPreferData(DataStoreKey.UserUID, userUUID)
        return userUUID
    }

    fun showDiaryBookDialog() {
        _diaryBookDialog.update { it.copy(isShow = true) }
    }

    fun dismissBookDialog() {
        _diaryBookDialog.update { it.copy(isShow = false) }
    }

    fun saveAndMakeBookInfo(
        imageFile: File?, title: String, description: String, monthType: DiaryPhaseType
    ) {
        viewModelScope.launch {
            if (!validateDiaryInfo(imageFile, title)) return@launch
            val newUserId = requestMakeId()
            var uploadImagePath: String? = null
            if (imageFile != null) {
                when (val result = oracleRepository.uploadImageFile(newUserId, imageFile).last()) {
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
                    _simpleMessage.emit(SimpleMessageState(this.message))
                }

                is CallResult.Success -> {
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
    val isShow: Boolean = false, val bookInfo: DiaryBookInfo = DiaryBookInfo(
        bookId = -1,
        title = "First Diary",
        description = "My First Diary",
        bookPhaseType = DiaryPhaseType.MOON,
        createDate = System.currentTimeMillis(),
        monthWritePercent = 0f,
    )
)
