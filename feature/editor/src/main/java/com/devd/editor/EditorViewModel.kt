package com.devd.editor

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devd.commonsystem.R
import com.devd.commonsystem.ui.calendar.CustomDatePickerDialogState
import com.devd.data.repository.DiaryBookRepository
import com.devd.data.repository.OracleRepository
import com.devd.datastore.DataStoreKey
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
import com.devd.model.local.CreateDiaryRequest
import com.devd.model.local.DiaryInfo
import com.devd.model.local.EditMode
import com.devd.model.local.FailUpload
import com.devd.model.local.SuccessUpload
import com.devd.model.local.UpdateDiaryRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

data class EditorUiState(
    var isShowLoading: Boolean = false,
    var editMode: EditMode = EditMode.Edit,
    var imageUrlForCrop: Uri? = null
)

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val oracleRepository: OracleRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val diaryBookRepository: DiaryBookRepository
) : ViewModel() {

    private val _editorUiState = MutableStateFlow(EditorUiState())
    val editorUiState: StateFlow<EditorUiState> get() = _editorUiState.asStateFlow()

    private val _messageDialog = MutableSharedFlow<MessageInfo>()
    val messageDialog: SharedFlow<MessageInfo> get() = _messageDialog.asSharedFlow()

    private val _customDatePickerDialogState = MutableStateFlow(CustomDatePickerDialogState())
    val customDatePickerDialogState: StateFlow<CustomDatePickerDialogState> get() = _customDatePickerDialogState.asStateFlow()

    private val _diaryInfoState = MutableStateFlow(DiaryInfoState(-1))
    val diaryInfoState: StateFlow<DiaryInfoState> get() = _diaryInfoState.asStateFlow()

    var shouldBackPage by mutableStateOf(false)


    fun initSelectDate(initDate: Long, editMode: EditMode) {
        _customDatePickerDialogState.update {
            it.copy(
                selectedDate = initDate,
                isCanChangeDate = editMode != EditMode.EditOnlyThis,
                onClickConfirm = ::changeSelectData,
                onClickCancel = ::dismissDatePickerDialog
            )
        }
    }

    fun initDiaryInfo(
        bookId: Long,
        diaryId: Long? = null,
        imageUrl: String? = null,
    ) {
        Timber.d("Call initDiaryInfo")
        viewModelScope.launch {
            diaryId?.let { // 편집
                val diary = diaryBookRepository.fetchDairiesByDiaryBook(bookId, it)
                    ?: return@launch _messageDialog.emit(
                        MessageInfo(
                            type = FAIL_LOAD_DIARY,
                            messageId = R.string.fail_fetch_diary_book
                        )
                    )
                _customDatePickerDialogState.update { calendar -> calendar.copy(selectedDate = diary.createdAt) }
                _diaryInfoState.update { diaryInfo ->
                    diaryInfo.copy(
                        bookId = bookId,
                        diaryId = diaryId,
                        imageUrl = Remote(diary.imageUrlList.first()),
                        diaryContents = diary.content,
                        diaryTag = diary.tagList
                    )
                }
            } ?: run {  // 신규
                _diaryInfoState.update {
                    it.copy(
                        bookId = bookId,
                        imageUrl = Local(imageUrl!!.toUri())
                    )
                }
            }
        }
    }

    fun updateImageUrl(uri: Uri) {
        _diaryInfoState.update { it.copy(imageUrl = Local(uri)) }
    }

    fun changeCropImageDialog(uri: Uri?) {
        _editorUiState.update { it.copy(imageUrlForCrop = uri) }
    }

    fun setDiaryText(text: String) {
        _diaryInfoState.update { it.copy(diaryContents = text) }
    }

    fun changeHashTag(add: String?, remove: String?) {
        _diaryInfoState.update {
            val newList = it.diaryTag.toMutableList()
            add?.let { newList.add(add) }
            remove?.let { newList.removeIf { item -> item == remove } }
            it.copy(diaryTag = newList)
        }
    }

    fun changeSelectData(dateMillis: Long) {
        viewModelScope.launch {
            val findItem = diaryBookRepository.fetchOneDiaryForDate(
                diaryBookId = diaryInfoState.value.bookId,
                date = dateMillis
            )
            updateDiaryInfo(findItem)
            _customDatePickerDialogState.update {
                it.copy(isShowDialog = false, selectedDate = dateMillis)
            }
        }
    }

    private fun updateDiaryInfo(diaryInfo: DiaryInfo? = null) {
        _diaryInfoState.update {
            it.copy(
                diaryId = diaryInfo?.diaryId,
                imageUrl = (diaryInfo?.let { Remote(diaryInfo.imageUrlList.first()) }),
                diaryContents = diaryInfo?.content ?: "",
                diaryTag = diaryInfo?.tagList ?: emptyList(),
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
            val userUUID = dataStoreRepository.getPreferData(DataStoreKey.UserUID) ?: return@launch
            _editorUiState.update { it.copy(isShowLoading = true) }
            _messageDialog.emit(MessageInfo(type = NONE))
            fileUrl?.let {  // fileUrl 이 있을 경우 서버에 업로드 필요!
                oracleRepository.uploadImageFile(
                    header = userUUID,
                    file = fileUrl
                ).collect { result ->
                    if (result is SuccessUpload) { // 이미지 업로드 성공
                        if (diaryInfoState.value.diaryId != null) modifyDiaryDataInDB("$userUUID/${result.uploadFileName}")
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

    fun showAskSavePopup() {
        viewModelScope.launch {
            if (!checkValidateDiaryInfo()) return@launch
            _messageDialog.emit(
                MessageInfo(type = ASK_SAVE, messageId = R.string.ask_save_diary_message)
            )
        }
    }

    private suspend fun checkValidateDiaryInfo(): Boolean {
        val diaryInfo = diaryInfoState.value
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
        return true
    }

    fun saveNewDiaryData(imageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val diaryInfo = diaryInfoState.value
            diaryBookRepository.insertNewDairyWithExtras(
                CreateDiaryRequest(
                    bookId = diaryInfo.bookId,
                    content = diaryInfo.diaryContents,
                    imageUrls = listOf(imageName),
                    tags = diaryInfo.diaryTag,
                    createDate = customDatePickerDialogState.value.selectedDate,
                    updateDate = System.currentTimeMillis()
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
            val diaryInfo = diaryInfoState.value
            val imageUrl = updateImage ?: (diaryInfo.imageUrl as Remote).url!!
            diaryBookRepository.updateDiaryWithExtras(
                UpdateDiaryRequest(
                    diaryId = diaryInfo.diaryId!!,
                    content = diaryInfo.diaryContents,
                    imageUrls = listOf(imageUrl),
                    tags = diaryInfo.diaryTag
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