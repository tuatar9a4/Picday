package com.devd.editor

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.devd.commonsystem.R
import com.devd.commonsystem.ui.calendar.CustomDatePickerDialogState
import com.devd.data.repository.DiaryBookRepository
import com.devd.data.repository.OracleRepository
import com.devd.datastore.DataStoreRepository
import com.devd.editor.data.ASK_SAVE
import com.devd.editor.data.DiaryInfoState
import com.devd.editor.data.FAIL_LOAD_DIARY
import com.devd.editor.data.Local
import com.devd.editor.data.MessageInfo
import com.devd.editor.data.NONE
import com.devd.editor.data.Remote
import com.devd.editor.data.SAVE_FAIL
import com.devd.editor.data.SAVE_SUCCESS
import com.devd.editor.data.SAVE_UPDATE
import com.devd.editor.navigation.EditorRoute
import com.devd.model.local.CreateDiaryRequest
import com.devd.model.local.DiaryBookInfo
import com.devd.model.local.DiaryInfo
import com.devd.model.local.EditMode
import com.devd.model.local.FailUpload
import com.devd.model.local.SuccessUpload
import com.devd.model.local.UpdateDiaryRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class EditorUiState(
    var isShowLoading: Boolean = false,
    var editMode: EditMode = EditMode.Edit,
    var diaryInfo: DiaryInfoState = DiaryInfoState(-1),
    var bookList: List<DiaryBookInfo> = emptyList(),
    var bookPos: Int = -1,
    var imageUrlForCrop: Uri? = null
) {
    val selectBookId get() = bookList.getOrNull(bookPos)?.bookId!!
}

@HiltViewModel
class EditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val oracleRepository: OracleRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val diaryBookRepository: DiaryBookRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<EditorRoute>()

    private val _editorUiState = MutableStateFlow(EditorUiState())
    val editorUiState: StateFlow<EditorUiState> get() = _editorUiState.asStateFlow()

    private val _messageDialog = MutableSharedFlow<MessageInfo>()
    val messageDialog: SharedFlow<MessageInfo> get() = _messageDialog.asSharedFlow()

    private val _customDatePickerDialogState = MutableStateFlow(CustomDatePickerDialogState())
    val customDatePickerDialogState: StateFlow<CustomDatePickerDialogState> get() = _customDatePickerDialogState.asStateFlow()

    var shouldBackPage by mutableStateOf(false)

    init {
        viewModelScope.launch {
            /* 일기장 리스틑 셋팅 */
            async { fetchBookList() }.await()
            /* 달력 셋팅 */
            initSelectDate()
            /* 일기 입력창 셋팅 */
            initDiaryInfo()

        }
    }

    private suspend fun fetchBookList() {
        val uuid = dataStoreRepository.getUserInfo()?.uuid!!
        val bookList = diaryBookRepository.fetchAllDiaryBooks(uuid)
        val currentBookPos = bookList.indexOfFirst { it.bookId == route.bookId }
        _editorUiState.update {
            it.copy(
                bookList = bookList,
                bookPos = currentBookPos
            )
        }
    }

    private fun initSelectDate() {
        _customDatePickerDialogState.update {
            it.copy(
                selectedDate = route.currentTime,
                isCanChangeDate = route.editMode != EditMode.EditOnlyThis,
                onClickConfirm = ::changeSelectData,
                onClickCancel = ::dismissDatePickerDialog
            )
        }
    }

    private suspend fun initDiaryInfo() {
        route.diaryId?.let { // 편집
            val diary =
                diaryBookRepository.fetchDairyByDiaryBook(editorUiState.value.selectBookId, it)
                    ?: return _messageDialog.emit(
                        MessageInfo(
                            type = FAIL_LOAD_DIARY,
                            messageId = R.string.fail_fetch_diary_book
                        )
                    )
            _customDatePickerDialogState.update { calendar -> calendar.copy(selectedDate = diary.createdAt) }
            _editorUiState.update { state ->
                state.copy(
                    diaryInfo = state.diaryInfo.copy(
                        bookId = editorUiState.value.selectBookId,
                        diaryId = route.diaryId,
                        imageUrl = Remote(diary.imageUrlList.first()),
                        diaryMood = diary.mood?:-1,
                        diaryContents = diary.content,
                        diaryTag = diary.tagList
                    )
                )
            }
        } ?: run {  // 신규
            _editorUiState.update { state ->
                state.copy(
                    diaryInfo = state.diaryInfo.copy(
                        bookId = editorUiState.value.selectBookId,
                        imageUrl = Local(route.imageUrl!!.toUri())
                    )
                )
            }
        }
    }

    fun updateImageUrl(uri: Uri) {
        _editorUiState.update { it.copy(diaryInfo = it.diaryInfo.copy(imageUrl = Local(uri))) }
    }

    fun changeCropImageDialog(uri: Uri?) {
        _editorUiState.update { it.copy(imageUrlForCrop = uri) }
    }

    fun setDiaryText(text: String) {
        _editorUiState.update { it.copy(diaryInfo = it.diaryInfo.copy(diaryContents = text)) }
    }

    fun changeHashTag(add: String?, remove: String?) {
        val diaryInfo = editorUiState.value.diaryInfo
        val newList = diaryInfo.diaryTag.toMutableList()
        add?.let { newList.add(add) }
        remove?.let { newList.removeIf { item -> item == remove } }

        _editorUiState.update {
            it.copy(
                diaryInfo = it.diaryInfo.copy(
                    diaryTag = newList
                )
            )
        }
    }

    fun changeSelectData(dateMillis: Long) {
        viewModelScope.launch {
            _editorUiState.update { it.copy(isShowLoading = true) }
            val findItem = diaryBookRepository.fetchOneDiaryForDate(
                diaryBookId = editorUiState.value.selectBookId,
                date = dateMillis
            )
            updateDiaryInfo(findItem)
            _customDatePickerDialogState.update {
                it.copy(isShowDialog = false, selectedDate = dateMillis)
            }
        }
    }

    private fun updateDiaryInfo(diaryInfo: DiaryInfo? = null) {
        _editorUiState.update {
            it.copy(
                isShowLoading = false,
                diaryInfo = it.diaryInfo.copy(
                    diaryId = diaryInfo?.diaryId,
                    imageUrl = (diaryInfo?.let { Remote(diaryInfo.imageUrlList.first()) }),
                    diaryContents = diaryInfo?.content ?: "",
                    diaryTag = diaryInfo?.tagList ?: emptyList()
                )
            )
        }
    }

    fun showDatePickerDialog() {
        _customDatePickerDialogState.update { it.copy(isShowDialog = true) }
    }

    fun dismissDatePickerDialog() {
        _customDatePickerDialogState.update { it.copy(isShowDialog = false) }
    }

    fun uploadImageToBuket(fileUrl: File?) {
        viewModelScope.launch {
            val userUUID = dataStoreRepository.getUserInfo()?.uuid ?: return@launch
            _editorUiState.update { it.copy(isShowLoading = true) }
            _messageDialog.emit(MessageInfo(type = NONE))
            fileUrl?.let {  // fileUrl 이 있을 경우 서버에 업로드 필요!
                oracleRepository.uploadImageFile(
                    header = userUUID,
                    file = fileUrl
                ).collect { result ->
                    if (result is SuccessUpload) { // 이미지 업로드 성공
                        if (editorUiState.value.diaryInfo.diaryId != null) modifyDiaryDataInDB("$userUUID/${result.uploadFileName}")
                        else saveNewDiaryData("$userUUID/${result.uploadFileName}")
                    } else if (result is FailUpload) {  // 이미지 업로드 실패[일기 저장실패]
                        _messageDialog.emit(
                            MessageInfo(type = SAVE_FAIL, messageStr = result.errorMessage)
                        )
                        _editorUiState.update { it.copy(isShowLoading = false) }
                    }
                }
            } ?: modifyDiaryDataInDB()
        }
    }

    fun changeBookPos(newPos: Int) {
        if (newPos == editorUiState.value.bookPos) return
        _editorUiState.update { it.copy(isShowLoading = true, bookPos = newPos) }
        val selectMillis = customDatePickerDialogState.value.selectedDate
        changeSelectData(selectMillis)
    }

    fun changeFeel(select : Int){
        _editorUiState.update { it.copy(diaryInfo = it.diaryInfo.copy(diaryMood = select)) }
    }

    fun showAskSavePopup() {
        viewModelScope.launch {
            if (!checkValidateDiaryInfo()) return@launch
            _messageDialog.emit(
                MessageInfo(type = ASK_SAVE, messageId = R.string.ask_save_diary_message)
            )
        }
    }

    private suspend fun checkValidateDiaryInfo(): Boolean {
        val diaryInfo = editorUiState.value.diaryInfo
        if (diaryInfo.imageUrl == null) { // 이미지 있어야지
            _messageDialog.emit(
                MessageInfo(type = SAVE_FAIL, messageId = R.string.request_diary_image_message)
            )
            return false
        }
        if (diaryInfo.diaryContents.isBlank() || diaryInfo.diaryContents.length < 2) { //내용이 있어야지 2자 이상
            _messageDialog.emit(
                MessageInfo(type = SAVE_FAIL, messageId = R.string.request_diary_contents_message)
            )
            return false
        }
        if (diaryInfo.diaryMood == -1) { //일기장 기분 미입력
            _messageDialog.emit(
                MessageInfo(type = SAVE_FAIL, messageId = R.string.request_diary_feel_message)
            )
            return false
        }
        return true
    }

    fun saveNewDiaryData(imageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val diaryInfo = editorUiState.value.diaryInfo
            diaryBookRepository.insertNewDairyWithExtras(
                CreateDiaryRequest(
                    bookId = editorUiState.value.selectBookId,
                    content = diaryInfo.diaryContents,
                    imageUrls = listOf(imageName),
                    tags = diaryInfo.diaryTag,
                    createDate = customDatePickerDialogState.value.selectedDate,
                    updateDate = System.currentTimeMillis(),
                    mood = diaryInfo.diaryMood
                )
            )
            _messageDialog.emit(
                MessageInfo(type = SAVE_SUCCESS, messageId = R.string.success_save_diary_message)
            )
            _editorUiState.update { it.copy(isShowLoading = false) }
        }
    }

    fun modifyDiaryDataInDB(updateImage: String? = null) {
        viewModelScope.launch {
            val diaryInfo = editorUiState.value.diaryInfo
            val imageUrl = updateImage ?: (diaryInfo.imageUrl as Remote).url!!
            diaryBookRepository.updateDiaryWithExtras(
                UpdateDiaryRequest(
                    diaryId = diaryInfo.diaryId!!,
                    content = diaryInfo.diaryContents,
                    imageUrls = listOf(imageUrl),
                    tags = diaryInfo.diaryTag,
                    mood = diaryInfo.diaryMood
                )
            )
            _messageDialog.emit(
                MessageInfo(type = SAVE_UPDATE, messageId = R.string.success_update_diary_message)
            )
            _editorUiState.update { it.copy(isShowLoading = false) }
        }
    }

    fun dismissMessageDialog() {
        viewModelScope.launch { _messageDialog.emit(MessageInfo(type = NONE)) }
    }
}