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
    var imageUrlForCrop : Uri? = null
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


    fun initSelectDate(initDate: Long) {
        _customDatePickerDialogState.update {
            it.copy(
                selectedDate = initDate,
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

    fun changeCropImageDialog(uri :Uri?){
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
        _customDatePickerDialogState.update {
            it.copy(isShowDialog = false, selectedDate = dateMillis)
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
            fileUrl?.let {
                oracleRepository.uploadImageFile(
                    header = userUUID,
                    file = fileUrl
                ).collect { result ->
                    if (result is SuccessUpload) {
                        if (_diaryInfoState.value.diaryId != null) updateDiaryData("$userUUID/${result.uploadFileName}")
                        else saveDiaryData("$userUUID/${result.uploadFileName}")
                    } else if (result is FailUpload) {
                        _messageDialog.emit(
                            MessageInfo(type = SAVE_FAIL, messageStr = result.errorMessage)
                        )
                        _editorUiState.update { it.copy(isShowLoading = false) }
                    }
                }
            } ?: updateDiaryData()
        }
    }

    fun showAskSavePopup() {
        viewModelScope.launch {
            _messageDialog.emit(
                MessageInfo(type = ASK_SAVE, messageId = R.string.ask_save_diary_message)
            )
        }
    }

    fun saveDiaryData(imageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val diaryInfo = diaryInfoState.value
            diaryBookRepository.saveDairyWithExtras(
                CreateDiaryRequest(
                    bookId = diaryInfo.bookId,
                    content = diaryInfo.diaryContents,
                    imageUrls = listOf(imageName),
                    tags = diaryInfo.diaryTag
                )
            )
            _messageDialog.emit(
                MessageInfo(type = SAVE_SUCCESS, messageId = R.string.success_save_diary_message)
            )
            _editorUiState.update { it.copy(isShowLoading = false) }
        }
    }

    fun updateDiaryData(updateImage: String? = null) {
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