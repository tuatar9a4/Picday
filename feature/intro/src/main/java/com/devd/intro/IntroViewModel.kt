package com.devd.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devd.data.repository.DiaryBookRepository
import com.devd.data.repository.OracleRepository
import com.devd.data.utils.CallResult
import com.devd.datastore.DataStoreKey
import com.devd.datastore.DataStoreRepository
import com.devd.firebase.fcm.FcmExtension
import com.devd.model.local.DiaryBookInfo
import com.devd.model.local.DiaryPhaseType
import com.devd.model.local.FailUpload
import com.devd.model.local.MessageData
import com.devd.model.local.StartUpload
import com.devd.model.local.SuccessUpload
import com.devd.model.local.Uploading
import com.devd.model.local.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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

data class IntroUiState(
    val isLoading: Boolean = false,

    val isShowBookDialog: Boolean = false,
    val isShowLockDialog: String? = null,
    val simpleMessage: MessageData? = null
)

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val diaryBookRepository: DiaryBookRepository,
    private val oracleRepository: OracleRepository,
) : ViewModel() {

    private val _introUiState = MutableStateFlow(IntroUiState())
    val introUiState get() = _introUiState.asStateFlow()

    var newBookInfo: DiaryBookInfo = DiaryBookInfo(bookId = -1, title = "", isMajor = true)

    private val _naviToHome = MutableSharedFlow<String>()
    val naviToHome get() = _naviToHome.asSharedFlow()

    init {
        viewModelScope.launch {
            Timber.d("existsUid() => ${fetchSavedNickName()}")
            _introUiState.update { it.copy(isLoading = true) }
            delay(1000)
            _introUiState.update { it.copy(isLoading = false) }
            if (fetchSavedNickName()) {
                if (!checkLockDiary()) _naviToHome.emit("")
            }
        }
    }

    fun showDiaryBookDialog() {
        _introUiState.update { it.copy(isShowBookDialog = true) }
    }

    fun dismissDiaryBookDialog() {
        _introUiState.update { it.copy(isShowBookDialog = false) }
    }

    fun saveAndMakeBookInfo(
        imageFile: File?,
        title: String,
        description: String,
        monthType: DiaryPhaseType,
        colorIndex: Int
    ) {
        viewModelScope.launch {
            if (!validateDiaryInfo(imageFile, title)) return@launch
            _introUiState.update { it.copy(isLoading = true, isShowBookDialog = false) }
            /* MakeNewUser */
            val savedUserInfo = dataStoreRepository.getUserInfo()
            Timber.d("CheckUserInfo! => $savedUserInfo")
            val userInfo = savedUserInfo ?: UserInfo(
                email = "",
                nickname = "",
                uuid = UUID.randomUUID().toString()
            )
            dataStoreRepository.setUserInfo(userInfo)

            val newUserId = userInfo.uuid
            var uploadImagePath: String? = null
            if (imageFile != null) {
                val res =
                    oracleRepository.fetchUploadUrl(newUserId, imageFile.name) ?: return@launch
                Timber.d("CheckUrl  uploadUrl => ${res}")
                when (val result =
                    oracleRepository.uploadImageFile(res.uploadUrl, imageFile).last()) {
                    is SuccessUpload -> uploadImagePath = "$newUserId/${result.uploadFileName}"
                    is FailUpload -> {
                        _introUiState.update {
                            it.copy(
                                simpleMessage = MessageData(
                                    messageType = "failUpload",
                                    messageStr = result.errorMessage
                                )
                            )
                        }
                    }

                    is Uploading, StartUpload -> Unit
                }
            }
            newBookInfo = newBookInfo.copy(
                bookImage = uploadImagePath,
                title = title,
                description = description,
                bookPhaseType = monthType,
                bookColor = colorIndex
            )
            insertDiaryBookWithUser(newUserId)
        }
    }

    private suspend fun insertDiaryBookWithUser(userUUID: String) {
        diaryBookRepository.insertNewDiaryBook(
            uuid = userUUID,
            bookImage = newBookInfo.bookImage!!,
            bookTitle = newBookInfo.title.trim(),
            bookDescription = newBookInfo.description ?: "",
            bookColor = newBookInfo.bookColor,
            bookPhaseType = newBookInfo.bookPhaseType.ordinal
        ).run {
            when (this) {
                is CallResult.NetworkError -> {
                    _introUiState.update {
                        it.copy(
                            isLoading = false,
                            simpleMessage = MessageData("failInsertBook", messageStr = this.message)
                        )
                    }
                }

                is CallResult.Success -> {
                    FcmExtension.getFcmToken()
                        ?.let { dataStoreRepository.setPreferData(DataStoreKey.FcmToken, it) }
                    _introUiState.update { it.copy(isLoading = false) }
                    _naviToHome.emit(userUUID)
                }
            }
        }
    }

    fun dismissSimpleMessageDialog() {
        _introUiState.update { it.copy(simpleMessage = null) }
    }


    suspend fun fetchSavedNickName(): Boolean {
        val savedUUID = dataStoreRepository.getUserInfo()?.uuid ?: return false
        return diaryBookRepository.hasDiaryBook(savedUUID)
    }

    suspend fun checkLockDiary(): Boolean {
        val lockPassword =
            dataStoreRepository.getPreferData(DataStoreKey.DiaryLockPassword) ?: return false
        _introUiState.update { it.copy(isShowLockDialog = lockPassword) }
        return true
    }

    fun passLockDiary() {
        viewModelScope.launch {
            _introUiState.update { it.copy(isShowLockDialog = null) }
            _naviToHome.emit("")
        }
    }

    fun showUnMatchPassword() {
        _introUiState.update {
            it.copy(
                simpleMessage = MessageData(
                    messageType = "unMathchPw",
                    messageStr = "비밀번호가 일치하지 않습니다."
                )
            )
        }
    }

    private fun validateDiaryInfo(bookImage: File?, title: String): Boolean {
        if (title.length < 2 || title.isBlank()) {
            _introUiState.update {
                it.copy(
                    simpleMessage = MessageData(
                        messageType = "titleError",
                        messageStr = "BookName 은 글자 2자 이상!"
                    )
                )
            }
            return false
        }

        if (bookImage == null) {
            _introUiState.update {
                it.copy(
                    simpleMessage = MessageData(
                        messageType = "titleError",
                        messageStr = "Book 대표 이미지를 선택해주세요"
                    )
                )
            }
            return false
        }
        return true
    }
}