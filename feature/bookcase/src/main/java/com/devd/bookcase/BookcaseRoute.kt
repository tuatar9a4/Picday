package com.devd.bookcase

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devd.bookcase.screen.DiaryBookActionButton
import com.devd.bookcase.screen.ExpandableDiaryBook
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.PrimaryColor
import com.devd.commonsystem.ui.Toolbar
import com.devd.model.local.DiaryBookInfo
import com.devd.model.local.DiaryPhaseType

sealed interface BookcaseInterface {
    data class OnOpenDiaryBook(val bookID: Long) : BookcaseInterface
    data object OnAddDiaryBook : BookcaseInterface
    data class OnDeleteDiaryBook(val bookID: Long) : BookcaseInterface
    data class OnUpdateDiaryBook(val bookID: Long) : BookcaseInterface
}

@Composable
fun BookcaseRoute(
    modifier: Modifier = Modifier,
    viewModel: BookcaseViewModel = hiltViewModel()
) {
    val uiState by viewModel.bookcaseUiState.collectAsState()

    BookcaseScreen(
        bookList = uiState.bookList,
        bookcaseInterface = { actionItem ->

        }
    )
}

@Preview
@Composable
fun BookcaseScreenPreview() {
    BookcaseScreen(
        bookList = listOf(
            DiaryBookInfo(
                bookId = 4246,
                bookImage = "habitasse",
                title = "postulant",
                description = "taciti",
                bookPhaseType = DiaryPhaseType.MOON,
                createDate = 9930,
                monthWritePercent = 22.23f
            )
        ),
        bookcaseInterface = {}
    )
}

@Composable
fun BookcaseScreen(
    modifier: Modifier = Modifier,
    bookList: List<DiaryBookInfo>,
    bookcaseInterface: (BookcaseInterface) -> Unit,
) {

    val pagerState = rememberPagerState(0) { bookList.size }

    Column(
        modifier = modifier.then(
            Modifier
                .fillMaxSize()
                .background(PrimaryColor)
        )
    ) {
        Toolbar(
            title = "",
            leftButtonIcon = R.drawable.icon_back_arrow,
            leftButtonClick = {}
        )
        Spacer(Modifier.height(40.dp))
        ExpandableDiaryBook(
            pagerState = pagerState,
            bookList = bookList,
            bookClickAction = bookcaseInterface
        )
        Spacer(Modifier.height(20.dp))
        DiaryBookActionButton(
            onAddDiaryBook = { bookcaseInterface(BookcaseInterface.OnAddDiaryBook) },
            onEditDiaryBook = { bookcaseInterface(BookcaseInterface.OnUpdateDiaryBook(bookList[pagerState.currentPage].bookId)) },
            onDeleteDiaryBook = { bookcaseInterface(BookcaseInterface.OnDeleteDiaryBook(bookList[pagerState.currentPage].bookId)) }
        )
    }

}