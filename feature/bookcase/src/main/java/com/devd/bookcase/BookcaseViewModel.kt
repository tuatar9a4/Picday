package com.devd.bookcase

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.devd.bookcase.navigation.BookcaseNaviRoute
import com.devd.data.repository.DiaryBookRepository
import com.devd.model.local.DiaryBookInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookcaseUiState(
    val isLoading: Boolean = false,
    val bookList: List<DiaryBookInfo> = emptyList(),
    val currentPos: Int? = null
)

@HiltViewModel
class BookcaseViewModel @Inject constructor(
    private val diaryBookRepository: DiaryBookRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val route = savedStateHandle.toRoute<BookcaseNaviRoute>()

    private val _bookcaseUiState = MutableStateFlow(BookcaseUiState())
    val bookcaseUiState get() = _bookcaseUiState.asStateFlow()

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

                val majorIndex =
                    bookcaseUiState.value.currentPos ?: bookItems.indexOfFirst { it.isMajor }

                _bookcaseUiState.update {
                    it.copy(
                        isLoading = false,
                        bookList = bookItems,
                        currentPos = it.currentPos ?: majorIndex
                    )
                }

            }
    }

}