package com.devd.bookcase

import android.app.Activity
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.devd.bookcase.data.ASK_DELETE_BOOK
import com.devd.bookcase.data.ASK_DELETE_DIARY_BOOK
import com.devd.bookcase.data.FAIL_SAVE_BOOK
import com.devd.bookcase.data.FAIL_UPDATE_BOOK
import com.devd.bookcase.data.LIMIT_BOOK_LIST_COUNT
import com.devd.bookcase.data.MessageInfo
import com.devd.bookcase.data.NEED_BOOK_IMAGE
import com.devd.bookcase.data.NEED_CAMERA_PERMISSION
import com.devd.bookcase.data.SUCCESS_SAVE_BOOK
import com.devd.bookcase.data.SUCCESS_UPDATE_BOOK
import com.devd.bookcase.screen.BookCoverItem
import com.devd.bookcase.screen.OpenedBook
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.BlackBCColor
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.BlackD9Color
import com.devd.commonsystem.theme.RedColor
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.ui.TextButton
import com.devd.commonsystem.ui.Toolbar
import com.devd.commonsystem.ui.cropImageDialog.ShowCropDialog
import com.devd.commonsystem.ui.dialog.OptionBottomSheet
import com.devd.commonsystem.ui.dialog.ShowImagePicker
import com.devd.commonsystem.ui.dialog.ShowMessageDialog
import com.devd.commonsystem.ui.dialog.book.DiaryBookDialog
import com.devd.commonsystem.ui.dialog.book.DiaryBookDialogType
import com.devd.commonsystem.ui.loading.LoadingDialog
import com.devd.commonsystem.utils.noRippleClickable
import com.devd.commonsystem.utils.preloadInterstitialAd
import com.devd.commonsystem.utils.rememberImagePicker
import com.devd.commonsystem.utils.rememberImageUrl
import com.devd.commonsystem.utils.shareBitmap
import com.devd.commonsystem.utils.uriToFile
import com.devd.model.local.DiaryBookInfo
import com.devd.model.local.DiaryInfo
import com.devd.model.local.DiaryPhaseType
import com.devd.model.local.SheetItem
import com.devd.permission.Consts
import com.devd.permission.IPermissionHandler
import com.devd.permission.rememberPermissionHandler
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

sealed interface BookcaseInterface {
    data class OnOpenDiaryBook(val bookInfo: DiaryBookInfo) : BookcaseInterface
    data object OnColesDiaryBook : BookcaseInterface
    data object OnAddDiaryBook : BookcaseInterface
    data class OnDeleteDiaryBook(val bookID: Long, val isMajor: Boolean) : BookcaseInterface
    data class OnUpdateDiaryBook(val bookInfo: DiaryBookInfo) : BookcaseInterface
    data class OnUpdateMajorBook(val bookID: Long) : BookcaseInterface

    data class OnDeleteDiary(val diaryId: Long) : BookcaseInterface
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

    /* ImagePicker */
    val showImagePicker = remember { mutableStateOf(false) }
    val showCropUi = remember { mutableStateOf<Uri?>(null) }
    val imagePicker = rememberImagePicker { uri ->
        uri?.let { showCropUi.value = uri }
    }

    var interstitialAd by remember { mutableStateOf<InterstitialAd?>(null) }

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
        if (uiState.selectBook != null) {
            viewModel.fetchDiaryListWithBook(uiState.selectBook!!)
        }
    }

    /* ADD */
    fun insertOrModifyBook(){
        val uri = viewModel.imageUrl
        if (uri != null) {
            val file = uri.let { context.uriToFile(it) }
            viewModel.uploadImage(file, bookInfo!!)
            bookInfo = null
        } else if (bookInfo!!.bookImage != null) {
            if (bookInfo!!.bookId == -1L) viewModel.insertDiaryBook(bookInfo!!)
            else viewModel.updateBookInfo(bookInfo!!)
            bookInfo = null
        }
    }

    fun addNewDiaryBook() {
        viewModel.visibleProgress(true)
        context.preloadInterstitialAd {
            interstitialAd = it
            Timber.d("CheckAD => interstitialAd load=>$interstitialAd")
            interstitialAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        Timber.d("CheckAD => Ad was dismissed.")
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        interstitialAd = null
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        // Called when fullscreen content failed to show.
                        Timber.d("CheckAD => Ad failed to show.")
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        interstitialAd = null
                    }

                    override fun onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        Timber.d("CheckAD => Ad showed fullscreen content.")
                    }

                    override fun onAdImpression() {
                        // Called when an impression is recorded for an ad.
                        insertOrModifyBook()
                        Timber.d("CheckAD => Ad recorded an impression.")
                    }

                    override fun onAdClicked() {
                        // Called when ad is clicked.
                        Timber.d("CheckAD => Ad was clicked.")
                    }
                }
            viewModel.visibleProgress(false)
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
        selectBook = uiState.selectBook,
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
                    viewModel.fetchDiaryListWithBook(actionItem.bookInfo)

                is BookcaseInterface.OnColesDiaryBook ->
                    viewModel.closeBook()

                is BookcaseInterface.OnUpdateMajorBook ->
                    viewModel.updateMajorBook(actionItem.bookID)

                is BookcaseInterface.OnDeleteDiary -> {
                    viewModel.requestDiary(actionItem.diaryId)
                }
            }
        },
        onChangeDiaryClick = { dairyPos ->
            scope.launch {
                val currentBook = uiState.diaryList.getOrNull(dairyPos)
                currentBook ?: return@launch checkPermission()
                onNaviToEditor(currentBook.diaryBookId, null, currentBook.diaryId)
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
                ASK_DELETE_DIARY_BOOK -> viewModel.deleteDiary()
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
            onSaveClick = { uri, title, description, monthType, color ->
                bookInfo = bookInfo!!.copy(
                    title = title,
                    description = description,
                    bookPhaseType = monthType
                )
                if( uri == null && bookInfo!!.bookImage == null){
                    viewModel.showMessageDialog(
                        MessageInfo(NEED_BOOK_IMAGE, R.string.request_diary_image_message)
                    )
                    return@DiaryBookDialog
                }
                viewModel.imageUrl = uri
                if ((bookInfo?.bookId ?: -1) == -1L && interstitialAd != null) {
                    interstitialAd?.show(context as Activity)
                } else {
                    insertOrModifyBook()
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

@Composable
fun BookcaseScreen(
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState(0) { 5 },
    bookList: List<DiaryBookInfo>,
    selectBook: DiaryBookInfo? = null,
    diaryList: List<DiaryInfo>,
    bookcaseInterface: (BookcaseInterface) -> Unit,
    onChangeDiaryClick: (Int) -> Unit,
    onBackPress: () -> Unit = {}
) {

    var isShowBookOptionSheet by remember { mutableStateOf<DiaryBookInfo?>(null) }
    var isShowDiaryOptionSheet by remember { mutableStateOf(false) }

    val diaryState = rememberPagerState(0) { diaryList.size }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val layerMap = remember { mutableMapOf<Int, GraphicsLayer>() }


    SharedTransitionLayout(modifier = modifier.then(Modifier.fillMaxWidth())) {
        Box() {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize(
                        animationSpec = spring(stiffness = Spring.StiffnessLow)
                    )
                    .verticalScroll(rememberScrollState())
                    .background(WhiteColor),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Toolbar(
                    titleBox = {
                        Text(
                            text = "",
                            style = MaterialTheme.typography.titleMedium.copy(color = BlackColor)
                        )
                    },
                )
                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = (LocalConfiguration.current.screenWidthDp.dp - 200.dp) / 2),
                    modifier = Modifier.fillMaxWidth(),
                    pageSpacing = 40.dp,
                    verticalAlignment = Alignment.CenterVertically,
                    pageSize = PageSize.Fixed(200.dp),
                ) { page ->
                    val bookInfo = bookList[page]
                    AnimatedVisibility(
                        visible = bookInfo != selectBook
                    ) {
                        bookInfo.bookImage?.rememberImageUrl()?.let {
                            BookCoverItem(
                                modifier = Modifier
                                    .sharedBounds(
                                        sharedContentState = rememberSharedContentState(key = "book_${bookInfo.bookId}"),
                                        animatedVisibilityScope = this@AnimatedVisibility
                                    )
                                    .noRippleClickable(
                                        onClick = {
                                            bookcaseInterface(
                                                BookcaseInterface.OnOpenDiaryBook(bookInfo)
                                            )
                                        }
                                    ),
                                coverImage = it,
                                bookSize = IntSize(200, 300),
                                isOpen = false,
                                bookInfo = bookInfo,
                                onMoreClick = { isShowBookOptionSheet = bookInfo },
                                onChangeMajor = {
                                    val bookId = bookInfo.bookId
                                    bookcaseInterface(
                                        BookcaseInterface.OnUpdateMajorBook(
                                            bookId
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
                if (selectBook == null) {
                    Spacer(Modifier.height(30.dp))
                    TextButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 50.dp),
                        contentsPadding = PaddingValues(vertical = 20.dp),
                        frontIcon = R.drawable.icon_plus,
                        text = "새 일기장",
                        textColor = BlackBCColor,
                        borerColor = BlackD9Color,
                        enableButtonColor = WhiteColor,
                        cornerRound = 34.dp,
                        onClick = { bookcaseInterface(BookcaseInterface.OnAddDiaryBook) }
                    )
                    Spacer(Modifier.height(20.dp))
                } else {
                    Spacer(Modifier.height(80.dp))
                }
            }

            OpenedBook(
                selectBook = selectBook,
                diaryList = diaryList,
                state = diaryState,
                layerMap = layerMap,
                bookClickAction = {
                    bookcaseInterface(it)
                },
                onDiaryMoreClick = {
                    isShowDiaryOptionSheet = true
                },
                onBackClick = onBackPress
            )
        }

    }
    isShowBookOptionSheet?.let { sheet ->
        OptionBottomSheet(
            "",
            items = listOf(
                SheetItem(
                    itemIcon = R.drawable.icon_pencil_stroke,
                    id = "0",
                    text = "일기장 수정",
                    itemColor = BlackColor
                ),
                SheetItem(
                    itemIcon = R.drawable.icon_delete_trash,
                    id = "1",
                    text = "삭제",
                    itemColor = RedColor
                )
            ),
            onItemSelected = {
                when (it.id) {
                    "0" -> {
                        bookcaseInterface(BookcaseInterface.OnUpdateDiaryBook(sheet))
                    }

                    "1" -> {
                        bookcaseInterface(
                            BookcaseInterface.OnDeleteDiaryBook(sheet.bookId, sheet.isMajor)
                        )
                    }
                }
                isShowBookOptionSheet = null
            }
        ) {
            isShowBookOptionSheet = null
        }
    }
    if (isShowDiaryOptionSheet) {
        OptionBottomSheet(
            "",
            items = listOf(
                SheetItem(
                    itemIcon = R.drawable.icon_pencil_stroke,
                    id = "0",
                    text = "일기장 수정",
                    itemColor = BlackColor
                ),
                SheetItem(
                    itemIcon = R.drawable.icon_share,
                    id = "1",
                    text = "공유하기",
                    itemColor = BlackColor
                ),
                SheetItem(
                    itemIcon = R.drawable.icon_delete_trash,
                    id = "2",
                    text = "삭제",
                    itemColor = RedColor
                )
            ),
            onItemSelected = {
                when (it.id) {
                    "0" -> {
                        onChangeDiaryClick(diaryState.currentPage)
                    }

                    "1" -> {
                        scope.launch {
                            val image = layerMap[diaryState.currentPage]?.toImageBitmap()
                            image?.let {
                                context.shareBitmap(it.asAndroidBitmap())
                            }
                        }
//                        bookcaseInterface(
//                            BookcaseInterface.OnDeleteDiaryBook(sheet.bookId, sheet.isMajor)
//                        )
                    }

                    "2" -> {
                        val diaryId = diaryList.getOrNull(diaryState.currentPage)?.diaryId
                            ?: return@OptionBottomSheet
                        bookcaseInterface(BookcaseInterface.OnDeleteDiary(diaryId))
                    }
                }
                isShowDiaryOptionSheet = false
            }
        ) {
            isShowDiaryOptionSheet = false
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
        diaryList = emptyList(),
        onChangeDiaryClick = {},
        bookcaseInterface = {}
    )
}