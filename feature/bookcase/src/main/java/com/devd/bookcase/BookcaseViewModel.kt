package com.devd.bookcase

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.devd.bookcase.data.FAIL_DELETE_BOOK
import com.devd.bookcase.data.FAIL_SAVE_BOOK
import com.devd.bookcase.data.FAIL_UPDATE_BOOK
import com.devd.bookcase.data.MessageInfo
import com.devd.bookcase.data.NONE
import com.devd.bookcase.data.SUCCESS_DELETE_BOOK
import com.devd.bookcase.data.SUCCESS_SAVE_BOOK
import com.devd.bookcase.data.SUCCESS_UPDATE_BOOK
import com.devd.bookcase.navigation.BookcaseNaviRoute
import com.devd.commonsystem.R
import com.devd.data.repository.DiaryBookRepository
import com.devd.data.repository.OracleRepository
import com.devd.data.utils.CallResult
import com.devd.datastore.DataStoreKey
import com.devd.datastore.DataStoreRepository
import com.devd.model.local.DiaryBookInfo
import com.devd.model.local.DiaryPhaseType
import com.devd.model.local.FailUpload
import com.devd.model.local.SuccessUpload
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

data class BookcaseUiState(
    val isLoading: Boolean = false,
    val bookList: List<DiaryBookInfo> = emptyList(),
    val messageDialog: MessageInfo = MessageInfo(type = NONE)
)

@HiltViewModel
class BookcaseViewModel @Inject constructor(
    private val diaryBookRepository: DiaryBookRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val oracleRepository: OracleRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val route = savedStateHandle.toRoute<BookcaseNaviRoute>()

    val newBookInfo = DiaryBookInfo(-1L, null, "Title", "Description", DiaryPhaseType.MOON, 0)

    private val _bookcaseUiState = MutableStateFlow(BookcaseUiState())
    val bookcaseUiState get() = _bookcaseUiState.asStateFlow()

    private val _scrollEvent = MutableSharedFlow<Int>()
    val scrollEvent get() = _scrollEvent.asSharedFlow()

    private val _uploadImageEvent = MutableSharedFlow<String>()
    val uploadImageEvent get() = _uploadImageEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            _bookcaseUiState.update { it.copy(isLoading = true) }
            collectDiaryBook()
        }
    }

    suspend fun collectDiaryBook() {
        diaryBookRepository.fetchAllDairies(route.userUUID)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2000L),
                initialValue = emptyList()
            ).collect { bookItems ->
                Timber.d("CheckChangeBook -> $bookItems")
                val pos = bookItems.indexOfFirst { it.isMajor }
                if (bookItems.isEmpty()) scrollToPosition(pos)

                _bookcaseUiState.update {
                    it.copy(
                        isLoading = false,
                        bookList = bookItems,
                    )
                }
            }
    }

    suspend fun scrollToPosition(pos: Int) {
        _scrollEvent.emit(pos)
    }

    fun showMessageDialog(messageInfo: MessageInfo) {
        _bookcaseUiState.update { it.copy(messageDialog = messageInfo) }
    }

    fun dismissMessageDialog() {
        _bookcaseUiState.update { it.copy(messageDialog = MessageInfo(NONE)) }
    }

    fun uploadImage(file: File) {
        viewModelScope.launch {
            val userUUID = dataStoreRepository.getPreferData(DataStoreKey.UserUID) ?: return@launch
            _bookcaseUiState.update { it.copy(isLoading = true) }
            oracleRepository.uploadImageFile(
                header = userUUID,
                file = file
            ).collect { result ->
                if (result is SuccessUpload) { // 이미지 업로드 성공
                    _uploadImageEvent.emit("$userUUID/${result.uploadFileName}")
                } else if (result is FailUpload) {  // 이미지 업로드 실패[일기 저장실패]
                    _bookcaseUiState.update {
                        it.copy(
                            isLoading = false,
                            messageDialog = MessageInfo(
                                type = FAIL_UPDATE_BOOK,
                                messageId = R.string.fail_update_diary_message
                            )
                        )
                    }
                }
            }
        }
    }

    fun insertDiaryBook(
        bookInfo: DiaryBookInfo
    ) {
        viewModelScope.launch {
            val userUUID = dataStoreRepository.getPreferData(DataStoreKey.UserUID) ?: return@launch
            diaryBookRepository.insertNewDiaryBook(
                uuid = userUUID,
                bookImage = bookInfo.bookImage!!,
                bookTitle = bookInfo.title.trim(),
                bookDescription = bookInfo.description!!
            ).run {
                when (this) {
                    is CallResult.NetworkError -> {
                        _bookcaseUiState.update {
                            it.copy(
                                isLoading = false,
                                messageDialog = MessageInfo(
                                    type = FAIL_SAVE_BOOK,
                                    messageId = R.string.fail_save_new_book_message
                                )
                            )
                        }
                    }

                    is CallResult.Success -> {
                        _bookcaseUiState.update {
                            it.copy(
                                isLoading = false,
                                messageDialog = MessageInfo(
                                    type = SUCCESS_SAVE_BOOK,
                                    messageId = R.string.success_save_new_book_message
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * uri : upload 해야하는 이미지 Uri, null 일 경우 변경 없는거
     * title : 일기장 title
     * description : 일기장 description
     * monthType : 일기장 한달 표현 타입
     */
    fun updateBookInfo(
        bookInfo: DiaryBookInfo
    ) {
        viewModelScope.launch {
            diaryBookRepository.updateBookInfo(bookInfo)?.let { // Fail
                _bookcaseUiState.update {
                    it.copy(
                        isLoading = false,
                        messageDialog = MessageInfo(
                            FAIL_UPDATE_BOOK,
                            messageId = R.string.fail_update_diary_message
                        )
                    )
                }
            } ?: run { //Success
                _bookcaseUiState.update {
                    it.copy(
                        isLoading = false,
                        messageDialog = MessageInfo(
                            SUCCESS_UPDATE_BOOK,
                            messageId = R.string.success_update_diary_message
                        )
                    )
                }
            }
        }
    }

    fun deleteDiaryBook(deleteID: Long) {
        _bookcaseUiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val errorMessage = diaryBookRepository.deleteBookInfo(deleteID)
            val (code, message) = if (errorMessage == null) {
                SUCCESS_DELETE_BOOK to R.string.success_delete_book_message
            } else {
                FAIL_DELETE_BOOK to R.string.fail_delete_book_message
            }
            _bookcaseUiState.update {
                it.copy(
                    isLoading = true,
                    messageDialog = MessageInfo(code, message)
                )
            }

        }
    }

}