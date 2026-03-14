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
import com.devd.bookcase.data.ASK_DELETE_BOOK
import com.devd.bookcase.data.FAIL_SAVE_BOOK
import com.devd.bookcase.data.FAIL_UPDATE_BOOK
import com.devd.bookcase.data.LIMIT_BOOK_LIST_COUNT
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
import com.devd.model.local.DiaryInfo
import com.devd.model.local.DiaryPhaseType
import kotlinx.coroutines.launch

sealed interface BookcaseInterface {
    data class OnOpenDiaryBook(val bookID: Long) : BookcaseInterface
    data object OnColesDiaryBook : BookcaseInterface
    data object OnAddDiaryBook : BookcaseInterface
    data class OnDeleteDiaryBook(val bookID: Long, val isMajor: Boolean) : BookcaseInterface
    data class OnUpdateDiaryBook(val bookInfo: DiaryBookInfo) : BookcaseInterface
}

@Composable
fun BookcaseRoute(
    modifier: Modifier = Modifier,
    viewModel: BookcaseViewModel = hiltViewModel(),
    onBackPress: () -> Unit
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
            viewModel.adResultEvent.collect { adResult ->
                if (adResult) bookInfo =
                    viewModel.newBookInfo.copy(createDate = System.currentTimeMillis())
            }
        }
    }

    /* ADD */
    fun addNewDiaryBook() {
        if (uiState.bookList.size >= 3) {   // 3개 초과는 광고보고 생성
            viewModel.showMessageDialog(
                MessageInfo(
                    type = LIMIT_BOOK_LIST_COUNT,
                    messageId = R.string.limit_book_list_count
                )
            )
        } else {
            bookInfo =
                viewModel.newBookInfo.copy(createDate = System.currentTimeMillis())
        }
    }

    /* MODIFY */
    fun modifyDiaryBook(modifyBookInfo: DiaryBookInfo) {
        bookInfo = modifyBookInfo
    }

    BookcaseScreen(
        modifier = modifier,
        bookList = uiState.bookList,
        diaryList = uiState.diaryList,
        pagerState = pagerState,
        bookcaseInterface = { actionItem ->
            when (actionItem) {
                BookcaseInterface.OnAddDiaryBook ->
                    addNewDiaryBook()

                is BookcaseInterface.OnDeleteDiaryBook ->
                    viewModel.requestDiaryBook(actionItem.bookID, actionItem.isMajor)

                is BookcaseInterface.OnUpdateDiaryBook ->
                    modifyDiaryBook(actionItem.bookInfo)

                is BookcaseInterface.OnOpenDiaryBook ->
                    viewModel.fetchDiaryListWithBook(actionItem.bookID)

                is BookcaseInterface.OnColesDiaryBook ->
                    viewModel.closeBook()
            }
        },
        onBackPress = onBackPress
    )

    uiState.isLoading.LoadingDialog()
    uiState.messageDialog.getMessage()?.ShowMessageDialog(
        rightButtonMessage =
            when (uiState.messageDialog.type) {
                LIMIT_BOOK_LIST_COUNT, ASK_DELETE_BOOK -> R.string.yes
                else -> R.string.confirm
            },
        onRightButtonClick = {
            when (uiState.messageDialog.type) {
                FAIL_UPDATE_BOOK, SUCCESS_UPDATE_BOOK,
                FAIL_SAVE_BOOK, SUCCESS_SAVE_BOOK -> bookInfo = null

                ASK_DELETE_BOOK -> viewModel.deleteDiaryBook()
                LIMIT_BOOK_LIST_COUNT -> viewModel.showAdVideo()
                else -> Unit
            }
            viewModel.dismissMessageDialog()
        },
        leftButtonMessage = when (uiState.messageDialog.type) {
            LIMIT_BOOK_LIST_COUNT, ASK_DELETE_BOOK -> R.string.no
            else -> null
        },
        onLeftButtonClick = {
            viewModel.dismissMessageDialog()
        }
    )
    if (bookInfo != null) {
        DiaryBookDialog(
            dialogType = DiaryBookDialogType.EDIT,
            bookInfo = bookInfo!!,
            onSaveClick = { uri, title, description, monthType ->
                bookInfo = bookInfo!!.copy(
                    title = title,
                    description = description,
                    bookPhaseType = monthType
                )
                if (uri != null) {
                    val file = uri.let { context.uriToFile(it) }
                    viewModel.uploadImage(file, bookInfo!!)
                } else if (bookInfo!!.bookImage != null) {
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
        diaryList = emptyList(),
        bookcaseInterface = {}
    )
}

@Composable
fun BookcaseScreen(
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState(0) { 5 },
    bookList: List<DiaryBookInfo>,
    diaryList: List<DiaryInfo>,
    bookcaseInterface: (BookcaseInterface) -> Unit,
    onBackPress: () -> Unit = {}
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
            leftButtonClick = onBackPress
        )
        Spacer(Modifier.height(40.dp))
        ExpandableDiaryBook(
            pagerState = pagerState,
            isOpened = isOpenBook,
            bookList = bookList,
            diaryList = diaryList,
            bookClickAction = bookcaseInterface
        )
        Spacer(Modifier.height(20.dp))
        if (!isOpenBook.value) {
            DiaryBookActionButton(
                onEditDiaryBook = { bookcaseInterface(BookcaseInterface.OnUpdateDiaryBook(bookList[pagerState.currentPage])) },
                onAddDiaryBook = { bookcaseInterface(BookcaseInterface.OnAddDiaryBook) },
                onDeleteDiaryBook = {
                    bookcaseInterface(
                        BookcaseInterface.OnDeleteDiaryBook(
                            bookList[pagerState.currentPage].bookId,
                            bookList[pagerState.currentPage].isMajor
                        )
                    )
                }
            )
        }
    }

}