package com.devd.bookcase

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.devd.bookcase.data.ASK_DELETE_BOOK
import com.devd.bookcase.data.FAIL_SAVE_BOOK
import com.devd.bookcase.data.FAIL_UPDATE_BOOK
import com.devd.bookcase.data.LIMIT_BOOK_LIST_COUNT
import com.devd.bookcase.data.MessageInfo
import com.devd.bookcase.data.NEED_BOOK_IMAGE
import com.devd.bookcase.data.NEED_CAMERA_PERMISSION
import com.devd.bookcase.data.SUCCESS_SAVE_BOOK
import com.devd.bookcase.data.SUCCESS_UPDATE_BOOK
import com.devd.bookcase.screen.ExpandableDiaryBook
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.PrimaryColor
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.ui.Toolbar
import com.devd.commonsystem.ui.cropImageDialog.ShowCropDialog
import com.devd.commonsystem.ui.dialog.ShowImagePicker
import com.devd.commonsystem.ui.dialog.ShowMessageDialog
import com.devd.commonsystem.ui.dialog.book.DiaryBookDialog
import com.devd.commonsystem.ui.dialog.book.DiaryBookDialogType
import com.devd.commonsystem.ui.loading.LoadingDialog
import com.devd.commonsystem.utils.rememberImagePicker
import com.devd.commonsystem.utils.uriToFile
import com.devd.model.local.DiaryBookInfo
import com.devd.model.local.DiaryInfo
import com.devd.model.local.DiaryPhaseType
import com.devd.permission.Consts
import com.devd.permission.IPermissionHandler
import com.devd.permission.rememberPermissionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface BookcaseInterface {
    data class OnOpenDiaryBook(val bookID: Long) : BookcaseInterface
    data object OnColesDiaryBook : BookcaseInterface
    data object OnAddDiaryBook : BookcaseInterface
    data class OnDeleteDiaryBook(val bookID: Long, val isMajor: Boolean) : BookcaseInterface
    data class OnUpdateDiaryBook(val bookInfo: DiaryBookInfo) : BookcaseInterface
    data class OnUpdateMajorBook(val bookID: Long) : BookcaseInterface
}

@Composable
fun BookcaseRoute(
    modifier: Modifier = Modifier,
    viewModel: BookcaseViewModel = hiltViewModel(),
    onNaviToEditor: (bookId: Long, imageUrl: String?, diaryId: Long?) -> Unit,
    onBackPress: () -> Unit
) {
    val uiState by viewModel.bookcaseUiState.collectAsState()

    val permissionHandler: IPermissionHandler = rememberPermissionHandler()
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    var bookInfo by remember { mutableStateOf<DiaryBookInfo?>(null) }

    val isOpenBook = remember { mutableStateOf(false) }

    /* ImagePicker */
    val showImagePicker = remember { mutableStateOf(false) }
    val showCropUi = remember { mutableStateOf<Uri?>(null) }
    val imagePicker = rememberImagePicker { uri ->
        uri?.let { showCropUi.value = uri }
    }

    suspend fun checkPermission() {
        val grant = permissionHandler.requestPermissionIfNeeded(Consts.CAMERA_PERMISSION)
        if (grant.any { !it.value }) {
            viewModel.showMessageDialog(
                MessageInfo(NEED_CAMERA_PERMISSION, R.string.need_camera_permission)
            )
        } else {
            showImagePicker.value = true
        }
    }

    val pagerState = rememberPagerState(0) { uiState.bookList.size }
    LaunchedEffect(Unit) {
        launch {
            snapshotFlow { uiState.bookList }
                .filter { it.isNotEmpty() }
                .first()
                .let { list ->
                    if (!viewModel.isInitScroll) return@launch
                    delay(150)
                    val pos = list.indexOfFirst { it.isMajor }
                    if (pos >= 0) pagerState.scrollToPage(pos)
                    viewModel.isInitScroll = false
                }
        }
        launch {
            viewModel.adResultEvent.collect { adResult ->
                if (adResult) bookInfo =
                    viewModel.newBookInfo.copy(createDate = System.currentTimeMillis())
            }
        }
    }

    BackHandler(enabled = true) {
        if(isOpenBook.value) isOpenBook.value = false
        else onBackPress()
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
        isOpenBook = isOpenBook,
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

                is BookcaseInterface.OnUpdateMajorBook ->
                    viewModel.updateMajorBook(actionItem.bookID)
            }
        },
        onAddDiaryPress = {
            scope.launch {
                val currentBookId = uiState.bookList[pagerState.currentPage].bookId
                val todayDiaryId = viewModel.hasWriteToDayDiary(currentBookId)
                todayDiaryId?.let {
                    onNaviToEditor(currentBookId, null, it)
                } ?: run {
                    checkPermission()
                }
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
            onSaveClick = { uri, title, description, monthType,color ->
                bookInfo = bookInfo!!.copy(
                    title = title,
                    description = description,
                    bookPhaseType = monthType
                )
                if (uri != null) {
                    val file = uri.let { context.uriToFile(it) }
                    viewModel.uploadImage(file, bookInfo!!)
                    bookInfo = null
                } else if (bookInfo!!.bookImage != null) {
                    if (bookInfo!!.bookId == -1L) viewModel.insertDiaryBook(bookInfo!!)
                    else viewModel.updateBookInfo(bookInfo!!)
                    bookInfo = null
                } else {
                    viewModel.showMessageDialog(
                        MessageInfo(NEED_BOOK_IMAGE, R.string.request_diary_image_message)
                    )
                }
            },
            onDismissRequest = { bookInfo = null }
        )
    }

    showImagePicker.value.ShowImagePicker(
        onCameraClick = imagePicker.launchCamera,
        onGalleryClick = imagePicker.launchGallery,
        onDismiss = { showImagePicker.value = false }
    )

    showCropUi.value?.ShowCropDialog { saveFile ->
        showCropUi.value = null
        saveFile?.let {
            val bookId = uiState.bookList[pagerState.currentPage].bookId
            onNaviToEditor(bookId, saveFile.toUri().toString(), null)
        }
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
        isOpenBook = remember { mutableStateOf(false) },
        diaryList = emptyList(),
        onAddDiaryPress = {},
        bookcaseInterface = {}
    )
}

@Composable
fun BookcaseScreen(
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState(0) { 5 },
    bookList: List<DiaryBookInfo>,
    isOpenBook: MutableState<Boolean>,
    diaryList: List<DiaryInfo>,
    bookcaseInterface: (BookcaseInterface) -> Unit,
    onAddDiaryPress: () -> Unit,
    onBackPress: () -> Unit = {}
) {

    Column(
        modifier = modifier.then(
            Modifier
                .fillMaxSize()
                .background(PrimaryColor)
        )
    ) {
        Toolbar(
            titleBox = {
                Text("", style = MaterialTheme.typography.titleMedium.copy(color = WhiteColor))
            },
            leftButtons = {
                if (!isOpenBook.value) {
                    Image(
                        modifier = Modifier
                            .size(32.dp)
                            .padding(4.dp)
                            .clickable(onClick = onBackPress),
                        painter = painterResource(R.drawable.icon_back_arrow),
                        contentDescription = null
                    )
                }

            },
            rightButtons = {
                if (!isOpenBook.value) {
                    Row() {
                        Image(
                            modifier = Modifier
                                .size(32.dp)
                                .padding(4.dp)
                                .clickable(onClick = { bookcaseInterface(BookcaseInterface.OnAddDiaryBook) }),
                            painter = painterResource(R.drawable.icon_plus),
                            contentDescription = null
                        )
                        Spacer(Modifier.width(15.dp))
                        Image(
                            modifier = Modifier
                                .size(32.dp)
                                .padding(4.dp)
                                .clickable(onClick = {
                                    bookcaseInterface(
                                        BookcaseInterface.OnDeleteDiaryBook(
                                            bookList[pagerState.currentPage].bookId,
                                            bookList[pagerState.currentPage].isMajor
                                        )
                                    )
                                }),
                            painter = painterResource(R.drawable.icon_delete_trash),
                            contentDescription = null
                        )
                    }
                }
            }
        )
        Spacer(Modifier.height(40.dp))
        ExpandableDiaryBook(
            pagerState = pagerState,
            isOpened = isOpenBook,
            bookList = bookList,
            diaryList = diaryList,
            bookClickAction = bookcaseInterface,
        )
        Spacer(Modifier.height(20.dp))
        if (!isOpenBook.value) {
            Image(
                modifier = Modifier
                    .shadow(elevation = 1.dp, shape = CircleShape)
                    .align(Alignment.CenterHorizontally)
                    .background(WhiteColor, CircleShape)
                    .size(52.dp)
                    .clickable(onClick = onAddDiaryPress)
                    .padding(14.dp),
                painter = painterResource(R.drawable.icon_pencil),
                contentDescription = null
            )
        }
    }
}