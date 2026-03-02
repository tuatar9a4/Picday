package com.devd.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devd.data.repository.DiaryBookRepository
import com.devd.model.local.DiaryInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class DiaryListUiState(
    val isLoading: Boolean = false,
    val isDiaryList: List<DiaryInfo> = emptyList(),
    val popupCode: PopupCode = PopupCode.NONE
)

sealed class PopupCode() {
    data object NONE : PopupCode()
    data object NotFoundItem : PopupCode()
    data class AskDeleteDiary(var confirmCallback: () -> Unit) : PopupCode()
}

@HiltViewModel
class DiaryListViewModel @Inject constructor(
    private val diaryBookRepository: DiaryBookRepository
) : ViewModel() {

    private val _diaryListUiState = MutableStateFlow(DiaryListUiState())
    val diaryListUiState = _diaryListUiState.asStateFlow()

    fun setInitDiaryList(initList: List<DiaryInfo>) {
        _diaryListUiState.update { it.copy(isDiaryList = initList) }
    }

    fun deleteDiaryWithId(id: Long?) {
        viewModelScope.launch(Dispatchers.IO) {
            id ?: return@launch _diaryListUiState.update {
                it.copy(popupCode = PopupCode.NotFoundItem, isLoading = false)
            }
            Timber.d("CheckDiaryList => ${id}")
            _diaryListUiState.update { it.copy(isLoading = true) }
            diaryBookRepository.deleteDiaryWithExtras(id)?.let { deleteItem ->
                _diaryListUiState.update {
                    val newList = it.isDiaryList.toMutableList()
                    newList.removeIf { item ->
                        Timber.d("CheckDiaryList removeIf => ${ item.diaryId } == ${ deleteItem.diaryId }")
                        item.diaryId == deleteItem.diaryId
                    }
                    Timber.d("CheckDiaryList newList => ${newList}")
                    it.copy(isDiaryList = newList, isLoading = false)
                }

            } ?: run {
                _diaryListUiState.update {
                    it.copy(popupCode = PopupCode.NotFoundItem, isLoading = false)
                }
            }
        }
    }

    fun showAskDeleteDiary(position: Int) {
        val diaryId = diaryListUiState.value.isDiaryList.getOrNull(position)?.diaryId
        _diaryListUiState.update {
            it.copy(
                popupCode = PopupCode.AskDeleteDiary(confirmCallback = {
                    dismissMessageDialog()
                    deleteDiaryWithId(diaryId)
                })
            )
        }
    }

    fun dismissMessageDialog() {
        _diaryListUiState.update { it.copy(popupCode = PopupCode.NONE) }
    }
}