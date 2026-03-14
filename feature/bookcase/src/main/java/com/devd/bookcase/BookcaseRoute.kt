package com.devd.bookcase

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devd.bookcase.data.FAIL_SAVE_BOOK
import com.devd.bookcase.data.FAIL_UPDATE_BOOK
import com.devd.bookcase.data.MessageInfo
import com.devd.bookcase.data.NEED_BOOK_IMAGE
import com.devd.bookcase.data.SUCCESS_SAVE_BOOK
import com.devd.bookcase.data.SUCCESS_UPDATE_BOOK
import com.devd.bookcase.screen.DiaryBookActionButton
import com.devd.bookcase.screen.ExpandableDiaryBook
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.PrimaryColor
import com.devd.commonsystem.ui.Toolbar
import com.devd.commonsystem.ui.dialog.DiaryBookDialog
import com.devd.commonsystem.ui.dialog.DiaryBookDialogType
import com.devd.commonsystem.ui.dialog.ShowMessageDialog
import com.devd.commonsystem.ui.loading.LoadingDialog
import com.devd.commonsystem.utils.uriToFile
import com.devd.model.local.DiaryBookInfo
import com.devd.model.local.DiaryPhaseType
import kotlinx.coroutines.launch

sealed interface BookcaseInterface {
    data class OnOpenDiaryBook(val bookID: Long) : BookcaseInterface
    data object OnAddDiaryBook : BookcaseInterface
    data class OnDeleteDiaryBook(val bookID: Long) : BookcaseInterface
    data class OnUpdateDiaryBook(val bookInfo: DiaryBookInfo) : BookcaseInterface
}

@Composable
fun BookcaseRoute(
    modifier: Modifier = Modifier,
    viewModel: BookcaseViewModel = hiltViewModel()
) {
    val uiState by viewModel.bookcaseUiState.collectAsState()

    val context = LocalContext.current
    var bookInfo by remember { mutableStateOf<DiaryBookInfo?>(null) }

    val pagerState = rememberPagerState(0) { uiState.bookList.size }
    LaunchedEffect(Unit) {
        launch {
            viewModel.scrollEvent.collect { pos ->
                pagerState.animateScrollToPage(pos)
            }
        }
        launch {
            viewModel.uploadImageEvent.collect { imageStr ->
                bookInfo = bookInfo!!.copy(bookImage = imageStr)
                if (bookInfo!!.bookId == -1L) viewModel.insertDiaryBook(bookInfo!!)
                else viewModel.updateBookInfo(bookInfo!!)
            }
        }
    }

    BookcaseScreen(
        modifier = modifier,
        bookList = uiState.bookList,
        pagerState = pagerState,
        bookcaseInterface = { actionItem ->
            when (actionItem) {
                BookcaseInterface.OnAddDiaryBook -> {
                    bookInfo = viewModel.newBookInfo.copy(createDate = System.currentTimeMillis())
                }

                is BookcaseInterface.OnDeleteDiaryBook -> {
                    viewModel.deleteDiaryBook(actionItem.bookID)
                }

                is BookcaseInterface.OnOpenDiaryBook -> {}
                is BookcaseInterface.OnUpdateDiaryBook -> {
                    bookInfo = actionItem.bookInfo
                }
            }
        }
    )

    uiState.isLoading.LoadingDialog()
    uiState.messageDialog.getMessage()?.ShowMessageDialog(
        onRightButtonClick = {
            when (uiState.messageDialog.type) {
                FAIL_UPDATE_BOOK, SUCCESS_UPDATE_BOOK,
                FAIL_SAVE_BOOK, SUCCESS_SAVE_BOOK -> bookInfo = null

                else -> Unit
            }
            viewModel.dismissMessageDialog()
        }
    )
    if (bookInfo != null) {
        DiaryBookDialog(
            dialogType = DiaryBookDialogType.EDIT,
            bookInfo = bookInfo!!,
            onSaveClick = { uri, title, description, monthType ->
                if (uri != null) {
                    val file = uri.let { context.uriToFile(it) }
                    viewModel.uploadImage(file)
                } else if (bookInfo!!.bookImage != null) {
                    bookInfo = bookInfo!!.copy(
                        title = title,
                        description = description,
                        bookPhaseType = monthType
                    )
                    if (bookInfo!!.bookId == -1L) viewModel.insertDiaryBook(bookInfo!!)
                    else viewModel.updateBookInfo(bookInfo!!)
                } else {
                    viewModel.showMessageDialog(
                        MessageInfo(NEED_BOOK_IMAGE, R.string.request_diary_image_message)
                    )
                }
            },
            onDismissRequest = { bookInfo = null }
        )
    }
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
        pagerState = rememberPagerState(0) { 1 },
        bookcaseInterface = {}
    )
}

@Composable
fun BookcaseScreen(
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState(0) { 5 },
    bookList: List<DiaryBookInfo>,
    bookcaseInterface: (BookcaseInterface) -> Unit,
) {
    val isOpenBook = remember { mutableStateOf(false) }

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
            isOpened = isOpenBook,
            bookList = bookList,
            bookClickAction = bookcaseInterface
        )
        Spacer(Modifier.height(20.dp))
        if (!isOpenBook.value) {
            DiaryBookActionButton(
                onEditDiaryBook = { bookcaseInterface(BookcaseInterface.OnUpdateDiaryBook(bookList[pagerState.currentPage])) },
                onAddDiaryBook = { bookcaseInterface(BookcaseInterface.OnAddDiaryBook) },
                onDeleteDiaryBook = { bookcaseInterface(BookcaseInterface.OnDeleteDiaryBook(bookList[pagerState.currentPage].bookId)) }
            )
        }
    }

}