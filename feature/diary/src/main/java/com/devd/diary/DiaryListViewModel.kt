package com.devd.diary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.devd.data.repository.DiaryBookRepository
import com.devd.diary.navigation.DiaryInfoListType
import com.devd.diary.navigation.DiaryListRoute
import com.devd.model.local.DiaryInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.reflect.typeOf

data class DiaryListUiState(
    val isLoading: Boolean = false,
    val diaryList: List<DiaryInfo> = emptyList(),
    val startPos: Int = 0,
    val popupCode: PopupCode = PopupCode.NONE
)

sealed class PopupCode() {
    data object NONE : PopupCode()
    data object NotFoundItem : PopupCode()
    data class AskDeleteDiary(var confirmCallback: () -> Unit) : PopupCode()
}

@HiltViewModel
class DiaryListViewModel @Inject constructor(
    private val diaryBookRepository: DiaryBookRepository,
    savedStateHandle: SavedStateHandle // 추가
) : ViewModel() {

    private val route = savedStateHandle.toRoute<DiaryListRoute>(
        typeMap = mapOf(typeOf<List<DiaryInfo>>() to DiaryInfoListType)
    )

    private val _diaryListUiState =
        MutableStateFlow(DiaryListUiState(diaryList = route.initList, startPos = route.startPos))
    val diaryListUiState = _diaryListUiState.asStateFlow()

    fun updateDiaryItem(diaryId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentList = _diaryListUiState.value.diaryList
            val oldItem = currentList.find { it.diaryId == diaryId } ?: return@launch

            diaryBookRepository.fetchDairyByDiaryBook(oldItem.diaryBookId, diaryId)
                ?.let { updatedItem ->
                    Timber.d("_diaryListUiState Before => ${ _diaryListUiState.value}")
                    _diaryListUiState.update { state ->
                        val newList = state.diaryList.map {
                            if (it.diaryId == diaryId) updatedItem else it
                        }
                        state.copy(diaryList = newList)
                    }
                    Timber.d("_diaryListUiState After => ${ _diaryListUiState.value}")
                }
        }
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
                    val newList = it.diaryList.toMutableList()
                    newList.removeIf { item ->
                        Timber.d("CheckDiaryList removeIf => ${item.diaryId} == ${deleteItem.diaryId}")
                        item.diaryId == deleteItem.diaryId
                    }
                    Timber.d("CheckDiaryList newList => ${newList}")
                    it.copy(diaryList = newList, isLoading = false)
                }

            } ?: run {
                _diaryListUiState.update {
                    it.copy(popupCode = PopupCode.NotFoundItem, isLoading = false)
                }
            }
        }
    }

    fun showAskDeleteDiary(position: Int) {
        val diaryId = diaryListUiState.value.diaryList.getOrNull(position)?.diaryId
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