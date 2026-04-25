package com.devd.home.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devd.commonsystem.R
import com.devd.commonsystem.ui.admob.BannerAd
import com.devd.commonsystem.ui.calendar.CustomDatePickerDialog
import com.devd.commonsystem.ui.cropImageDialog.ShowCropDialog
import com.devd.commonsystem.ui.dialog.ShowImagePicker
import com.devd.commonsystem.ui.dialog.ShowMessageDialog
import com.devd.commonsystem.ui.dialog.book.DiaryBookDialog
import com.devd.commonsystem.ui.dialog.book.DiaryBookDialogType
import com.devd.commonsystem.ui.loading.LoadingDialog
import com.devd.commonsystem.utils.centerItemIndex
import com.devd.commonsystem.utils.isCurrentMonth
import com.devd.commonsystem.utils.rememberImagePicker
import com.devd.home.BuildConfig
import com.devd.home.HomeUiState
import com.devd.home.HomeViewModel
import com.devd.model.local.DiaryInfo
import com.devd.permission.Consts
import com.devd.permission.IPermissionHandler
import com.devd.permission.rememberPermissionHandler
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlinx.coroutines.launch
import java.io.File


@Composable
fun HomeScreenRoute(
    modifier: Modifier = Modifier,
    onEditorMove: (imageUrl: String?, bookId: Long, diaryId: Long?) -> Unit = { _, _, _ -> },
    onSettingClick: (userId: String) -> Unit = {},
    onMoveDiaryList: (List<DiaryInfo>, Int) -> Unit = { _, _ -> },
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.homeUiState.collectAsStateWithLifecycle()

    val permissionHandler: IPermissionHandler = rememberPermissionHandler()
    val scope = rememberCoroutineScope()

    val diaryState = rememberLazyListState()

    val imagePicker = rememberImagePicker { uri ->
        uri?.let { viewModel.changeCropImageDialog(it) }
    }

    suspend fun checkPermission() {
        val grant = permissionHandler.requestPermissionIfNeeded(Consts.CAMERA_PERMISSION)
        if (grant.any { !it.value }) {
            viewModel.showMessageDialog(R.string.need_camera_permission)
        } else {
            viewModel.showImagePickerDialog()
        }
    }

    fun moveToEditor(cropFile: File?, diaryId: Long?) {
        onEditorMove.invoke(
            cropFile?.toUri()?.toString(),
            uiState.bookInfo?.bookId ?: 0,
            diaryId
        )
    }

    fun moveToDiaryList(list: List<DiaryInfo>, originalIndex: Int) {
        var selectPos = originalIndex
        val filteredList = list.filter {
            val isAddItem = it.diaryId == -1L
            if (isAddItem) selectPos--
            !isAddItem
        }
        onMoveDiaryList.invoke(filteredList, selectPos)
    }

    fun checkDiaryInfoBeforeMove() {
        scope.launch {
            if (!uiState.searchDate.isCurrentMonth()) {
                viewModel.fetchTodayDiary()?.diaryId
            } else {
                val selectIndex = diaryState.centerItemIndex() ?: -1
                uiState.diaryList.getOrNull(selectIndex)?.diaryId?.takeIf { id -> id != -1L }
            }?.let {
                moveToEditor(null, it)
            } ?: run {
                checkPermission()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchMainDiaryBook()
    }

    LaunchedEffect(Unit) {
        viewModel.scrollPosition.collect { index ->
            if (index == -1) return@collect
            diaryState.scrollToItem(index)
        }
    }

    HomeScreen(
        modifier = modifier,
        uiState = uiState,
        diaryState = diaryState,
        onShowCalendar = viewModel::showCalendarDialog,
        onEditorClick = { checkDiaryInfoBeforeMove() },
        onSettingClick = {
            onSettingClick(viewModel.storedUUID!!)
        },
        onDiaryCardClick = { moveToDiaryList(uiState.diaryList, it) },
        onBookClick = viewModel::showBookDialog
    )

    uiState.getDialogMessage()
        ?.ShowMessageDialog(onRightButtonClick = viewModel::dismissMessageDialog)

    uiState.isShowImagePicker.ShowImagePicker(
        onCameraClick = imagePicker.launchCamera,
        onGalleryClick = imagePicker.launchGallery,
        onDismiss = viewModel::dismissImagePickerDialog
    )
    uiState.isLoading.LoadingDialog()
    uiState.uriForCrop?.ShowCropDialog { saveFile ->
        viewModel.changeCropImageDialog(null)
        saveFile?.let { moveToEditor(saveFile, null) }
    }
    if (uiState.isShowCalendar) {
        CustomDatePickerDialog(
            title = "검색할 날짜를 선택해주세요",
            initDateMillis = uiState.searchDate.toInstant().toEpochMilli(),
            onSelectDate = viewModel::changeSearchMonth,
            onClickCancel = viewModel::dismissCalendar
        )
    }
    if (uiState.isShowBookDialog) {
        DiaryBookDialog(
            dialogType = DiaryBookDialogType.VIEW,
            bookInfo = uiState.bookInfo!!,
            onDismissRequest = viewModel::dismissBookDialog
        )
    }
}


@Preview
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState = HomeUiState(),
    diaryState: LazyListState = rememberLazyListState(),
    onShowCalendar: () -> Unit = {},
    onEditorClick: () -> Unit = {},
    onDiaryCardClick: (Int) -> Unit = {},
    onSettingClick: () -> Unit = {},
    onBookClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val adId =  if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/9214589741"
    else stringResource(R.string.admobBannerId)
    val adView = remember { AdView(context).apply {
        setAdSize(AdSize.BANNER)
        adUnitId = adId
        loadAd(AdRequest.Builder().build())
    } }

    Box(
        modifier = modifier.then(
            Modifier.fillMaxWidth()
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            Toolbar(
//                titleBox = {
//                    Text("", style = MaterialTheme.typography.titleMedium.copy(color = WhiteColor))
//                },
//                rightButtons = {
//                    Image(
//                        modifier = Modifier
//                            .size(36.dp)
//                            .padding(4.dp)
//                            .clickable(onClick = onSettingClick),
//                        painter = painterResource(R.drawable.icon_setting),
//                        contentDescription = null,
//                        colorFilter = ColorFilter.tint(color = BlackD9Color)
//                    )
//                }
//            )
            Box(modifier = Modifier.fillMaxWidth()) { BannerAd(adView, Modifier.fillMaxWidth()) }
            Spacer(Modifier.height(4.dp))
            BookCardScreen(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                bookInfo = uiState.bookInfo,
                onBookClick = onBookClick
            )   // 일기장 카드
            Spacer(Modifier.height(20.dp))
            YearCategory(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                searchDate = uiState.searchDate,
                onClick = onShowCalendar
            )   // 년도 선택 스크린
            Spacer(Modifier.height(15.dp))
            DiaryListScreen(
                modifier = Modifier,
                diaryState = diaryState,
                diaryList = uiState.diaryList,
                isCurrentMonth = uiState.searchDate.isCurrentMonth(),
                onDiaryCardClick = onDiaryCardClick,
                onAddCardClick = onEditorClick
            )   // DiaryList 스크린
            Spacer(Modifier.height(20.dp))
        }
    }
}