package com.devd.editor

import android.net.Uri
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
import com.devd.editor.data.MessageInfo
import com.devd.editor.data.SAVE_FAIL
import com.devd.editor.data.SAVE_SUCCESS
import com.devd.model.local.CreateDiaryRequest
import com.devd.model.local.FailUpload
import com.devd.model.local.SuccessUpload
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
import java.io.File
import javax.inject.Inject

data class EditorUiState(
    var isShowLoading: Boolean = false,
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
        imageUrl: Uri? = null,
    ) {
        _diaryInfoState.update {
            it.copy(
                bookId = bookId,
                diaryId = diaryId,
                imageUrl = imageUrl
            )
        }
    }

    fun updateImageUrl(uri: Uri) {
        _diaryInfoState.update { it.copy(imageUrl = uri) }
    }

    fun setDiaryText(text: String) {
        _diaryInfoState.update { it.copy(diaryContents = text) }
    }

    fun changeHashTag(tagList: List<String>) {
        _diaryInfoState.update { it.copy(diaryTag = tagList) }
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

    suspend fun uploadImageToBuket(fileUrl: File) {
        val userUUID = dataStoreRepository.getPreferData(DataStoreKey.UserUID) ?: return
        _editorUiState.update { it.copy(isShowLoading = true) }
        oracleRepository.uploadImageFile(
            header = userUUID,
            file = fileUrl
        ).collect { result ->
            if (result is SuccessUpload) saveDiaryData("$userUUID/${result.uploadFileName}")
            else if (result is FailUpload) {
                _messageDialog.emit(
                    MessageInfo(type = SAVE_FAIL, messageStr = result.errorMessage)
                )
                _editorUiState.update { it.copy(isShowLoading = false) }
            }
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
}