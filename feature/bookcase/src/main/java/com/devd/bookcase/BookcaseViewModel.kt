package com.devd.bookcase

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.devd.bookcase.data.ASK_DELETE_BOOK
import com.devd.bookcase.data.CAN_NOT_DELETE_MAJOR
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
import com.devd.datastore.DataStoreRepository
import com.devd.model.local.DiaryBookInfo
import com.devd.model.local.DiaryInfo
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
    val diaryList: List<DiaryInfo> = emptyList(),
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

    val newBookInfo = DiaryBookInfo(bookId = -1L, title = "")

    var storeDeleteId: Long? = null
    var isInitScroll = true

    private val _bookcaseUiState = MutableStateFlow(BookcaseUiState())
    val bookcaseUiState get() = _bookcaseUiState.asStateFlow()

    private val _adResultEvent = MutableSharedFlow<Boolean>()
    val adResultEvent get() = _adResultEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            _bookcaseUiState.update { it.copy(isLoading = true) }
            collectDiaryBook()
        }
    }

    suspend fun collectDiaryBook() {
        diaryBookRepository.fetchAllDairyBooksFlow(route.userUUID)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2000L),
                initialValue = emptyList()
            ).collect { bookItems ->
                Timber.d("CheckChangeBook -> $bookItems")
                _bookcaseUiState.update {
                    it.copy(
                        isLoading = false,
                        bookList = bookItems,
                    )
                }
            }
    }

    fun fetchDiaryListWithBook(bookId: Long) {
        viewModelScope.launch {
            _bookcaseUiState.update { it.copy(isLoading = true) }
            val diaryList = diaryBookRepository.fetchAllDairiesByDiaryBook(bookId)
            Timber.d("CheckDiary List => $diaryList")
            _bookcaseUiState.update { it.copy(isLoading = false, diaryList = diaryList) }
        }
    }

    fun closeBook() {
        _bookcaseUiState.update { it.copy(diaryList = emptyList()) }
    }

    fun showMessageDialog(messageInfo: MessageInfo) {
        _bookcaseUiState.update { it.copy(messageDialog = messageInfo) }
    }

    fun dismissMessageDialog() {
        _bookcaseUiState.update { it.copy(messageDialog = MessageInfo(NONE)) }
    }

    fun showAdVideo() {
        //TODO : 광고 보는거 나중에 추가
        viewModelScope.launch { _adResultEvent.emit(true) }
    }

    fun requestDiaryBook(deleteID: Long, isMajor: Boolean) {
        val (type, message) = if (isMajor) CAN_NOT_DELETE_MAJOR to R.string.cannot_delete_major
        else ASK_DELETE_BOOK to R.string.ask_delete_diary_message

        storeDeleteId = deleteID
        showMessageDialog(MessageInfo(type, message))

    }

    fun updateMajorBook(bookId: Long) {
        viewModelScope.launch {
            _bookcaseUiState.update { it.copy(isLoading = true) }
            diaryBookRepository.changeMajorBook(bookId, route.userUUID)
            _bookcaseUiState.update { it.copy(isLoading = false) }
        }
    }

    suspend fun hasWriteToDayDiary(bookId: Long): Long? {
        val todayDiary =
            diaryBookRepository.fetchOneDiaryForDate(bookId, System.currentTimeMillis())
        return todayDiary?.diaryId
    }

    /**
     * file : 업로드 이미지 파일
     */
    fun uploadImage(file: File, updateBookInfo: DiaryBookInfo) {
        viewModelScope.launch {
            val userUUID = dataStoreRepository.getUserInfo()?.uuid ?: return@launch
            _bookcaseUiState.update { it.copy(isLoading = true) }
            oracleRepository.uploadImageFile(
                header = userUUID,
                file = file
            ).collect { result ->
                if (result is SuccessUpload) { // 이미지 업로드 성공
                    val book = updateBookInfo.copy(bookImage = "$userUUID/${result.uploadFileName}")
                    if (book.bookId == -1L) insertDiaryBook(book) else updateBookInfo(book)
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
            val userUUID = dataStoreRepository.getUserInfo()?.uuid ?: return@launch
            diaryBookRepository.insertNewDiaryBook(
                uuid = userUUID,
                bookImage = bookInfo.bookImage!!,
                bookTitle = bookInfo.title.trim(),
                bookDescription = bookInfo.description!!,
                bookPhaseType = bookInfo.bookPhaseType.ordinal,
                bookColor = bookInfo.bookColor
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

    fun deleteDiaryBook() {
        storeDeleteId ?: return
        _bookcaseUiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val errorMessage = diaryBookRepository.deleteBookInfo(storeDeleteId!!)
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