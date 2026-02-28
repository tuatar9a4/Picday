package com.devd.diary

import androidx.lifecycle.ViewModel
import com.devd.model.local.DiaryInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class DiaryListUiState(
    val isLoading: Boolean = false,
    val isDiaryList: List<DiaryInfo> = emptyList()
)

@HiltViewModel
class DiaryListViewModel @Inject constructor(

) : ViewModel() {

    private val _diaryListUiState = MutableStateFlow(DiaryListUiState())
    val diaryListUiState = _diaryListUiState.asStateFlow()

    fun setInitDiaryList(initList: List<DiaryInfo>) {
        _diaryListUiState.update { it.copy(isDiaryList = initList) }
    }

}