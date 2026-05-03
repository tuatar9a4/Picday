package com.devd.editor.screen

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.Black33Color
import com.devd.commonsystem.theme.Black88Color
import com.devd.commonsystem.theme.BlackF4Color
import com.devd.commonsystem.theme.TransParents
import com.devd.commonsystem.ui.TextButton
import com.devd.commonsystem.ui.calendar.CustomDatePickerDialog
import com.devd.commonsystem.ui.calendar.RangeType
import com.devd.commonsystem.ui.cropImageDialog.ShowCropDialog
import com.devd.commonsystem.ui.dialog.OptionBottomSheet
import com.devd.commonsystem.ui.dialog.ShowImagePicker
import com.devd.commonsystem.ui.dialog.ShowMessageDialog
import com.devd.commonsystem.ui.loading.LoadingDialog
import com.devd.commonsystem.utils.FeelData
import com.devd.commonsystem.utils.noRippleClickable
import com.devd.commonsystem.utils.optimizeUriToFile
import com.devd.commonsystem.utils.preloadInterstitialAd
import com.devd.commonsystem.utils.rememberImagePicker
import com.devd.editor.EditorViewModel
import com.devd.editor.data.ASK_SAVE
import com.devd.editor.data.DiaryInfoState
import com.devd.editor.data.Local
import com.devd.editor.data.SAVE_SUCCESS
import com.devd.editor.data.SAVE_UPDATE
import com.devd.editor.screen.dialog.FeelBottomSheet
import com.devd.model.local.DiaryBookInfo
import com.devd.model.local.SheetItem
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import kotlin.random.Random

@Composable
fun rememberImeBottomSize(): Int {
    val density = LocalDensity.current
    return WindowInsets.ime.getBottom(density)
}

@Composable
fun EditorScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: EditorViewModel = hiltViewModel(),
    onBackIconClick: (id: Long?) -> Unit
) {
    val context = LocalContext.current

    /* UiState */
    val uiState by viewModel.editorUiState.collectAsStateWithLifecycle()

    var interstitialAd by remember { mutableStateOf<InterstitialAd?>(null) }

    /* Resolvation Move Back */
    LaunchedEffect(viewModel.shouldBackPage) {
        if (viewModel.shouldBackPage) {
            onBackIconClick.invoke(uiState.diaryInfo.diaryId)
            viewModel.shouldBackPage = false
        }
        context.preloadInterstitialAd {
            interstitialAd = it
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    interstitialAd = null
                }

                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                }

                override fun onAdImpression() {
                    val file =
                        (uiState.diaryInfo.imageUrl as? Local)?.uri?.let { context.optimizeUriToFile(it) }
                    viewModel.uploadImageToBuket(fileUrl = file)
                }
            }
        }
    }

    /* MessageDialog */
    val messageDialog by viewModel.messageDialog.collectAsStateWithLifecycle(null)

    /* ImagePicker */
    var isShowPickerDialog by remember { mutableStateOf(false) }
    val imagePicker = rememberImagePicker { uri ->
        viewModel.changeCropImageDialog(uri)
    }

    /* CalendarDialog */
    val customDatePickerDialogState by viewModel.customDatePickerDialogState.collectAsState()

    /* BookSelectPopup */
    var isShowBookSelect by remember { mutableStateOf(false) }

    EditorScreen(
        modifier = modifier,
        writeDate = customDatePickerDialogState.selectedDate,
        isCanChangeDate = customDatePickerDialogState.isCanChangeDate,
        diaryState = uiState.diaryInfo,
        onShowImagePicker = { isShowPickerDialog = true },
        selectDiary = uiState.bookList.getOrNull(uiState.bookPos),
        onBookTitleClick = { isShowBookSelect = true },
        onChangeDiaryText = viewModel::setDiaryText,
        onChangeFeel = viewModel::changeFeel,
        onChangeHashTag = viewModel::changeHashTag,
        onSaveDairy = viewModel::showAskSavePopup,
        onBackIconClick = { onBackIconClick(null) },
        onChangeCalendar = viewModel::showDatePickerDialog,
    )

    /* MessageDialog */
    messageDialog?.getMessage()?.ShowMessageDialog(
        leftButtonMessage = if (messageDialog?.type == ASK_SAVE) R.string.cancel else null,
        onLeftButtonClick = { viewModel.dismissMessageDialog() },
        rightButtonMessage = R.string.confirm,
        onRightButtonClick = {
            when (messageDialog?.type) {
                SAVE_SUCCESS, SAVE_UPDATE -> {
                    viewModel.dismissMessageDialog()
                    viewModel.shouldBackPage = true
                }

                ASK_SAVE -> {
                    val isSuccess = Random.nextFloat() < 0.3f
                    if (isSuccess && interstitialAd != null) {
                        interstitialAd?.show(context as Activity)
                    } else {
                        val file =
                            (uiState.diaryInfo.imageUrl as? Local)?.uri?.let { context.optimizeUriToFile(it) }
                        viewModel.uploadImageToBuket(fileUrl = file)
                    }
                }

                else -> {
                    viewModel.dismissMessageDialog()
                }
            }
        }
    )

    /* ImagePicker */
    isShowPickerDialog.ShowImagePicker(
        onCameraClick = imagePicker.launchCamera,
        onGalleryClick = imagePicker.launchGallery,
        onDismiss = { isShowPickerDialog = false }
    )
    uiState.imageUrlForCrop?.ShowCropDialog { cropFile ->
        viewModel.changeCropImageDialog(null)
        cropFile?.let { viewModel.updateImageUrl(it.toUri()) }
    }

    /* CalendarDialog */
    if (customDatePickerDialogState.isShowDialog) {
        CustomDatePickerDialog(
            title = "일기 작성 날짜를 선택하세요",
            selectRange = RangeType.CURRENT_MONTH,
            initDateMillis = customDatePickerDialogState.selectedDate,
            onSelectDate = customDatePickerDialogState.onClickConfirm,
            onClickCancel = customDatePickerDialogState.onClickCancel
        )
    }

    /* BookSelectPopup */
    if (isShowBookSelect) {
        OptionBottomSheet(
            title = "Select Book",
            items = uiState.bookList.mapIndexed { index, info ->
                SheetItem(
                    id = index.toString(),
                    text = info.title,
                    isSelected = index == uiState.bookPos
                )
            },
            onItemSelected = { item ->
                viewModel.changeBookPos(item.id.toInt())
                isShowBookSelect = false
            },
            onDismissRequest = { isShowBookSelect = false }
        )
    }

    /* LoadingDialog */
    uiState.isShowLoading.LoadingDialog()   //TODO : MoveMainActivity로 옮기기
}

@Preview
@Composable
fun EditorScreen(
    modifier: Modifier = Modifier,
    writeDate: Long = System.currentTimeMillis(),
    isCanChangeDate: Boolean = true,
    diaryState: DiaryInfoState? = null,
    selectDiary: DiaryBookInfo? = null,
    onShowImagePicker: () -> Unit = {},
    onBookTitleClick: () -> Unit = {},
    onSaveDairy: () -> Unit = {},
    onChangeFeel: (Int) -> Unit = {},
    onBackIconClick: () -> Unit = {},
    onChangeCalendar: () -> Unit = {},
    onChangeDiaryText: (String) -> Unit = {},
    onChangeHashTag: (String?, String?) -> Unit = { _, _ -> }
) {

    val contentsTextState = rememberTextFieldState(diaryState?.diaryContents ?: "z")

    var isShowFeelSheet by remember { mutableStateOf(false) }

    val hashTagList = remember { mutableStateListOf<String>() }
    val focusManger = LocalFocusManager.current
    val scrollState = rememberScrollState()

    LaunchedEffect(diaryState?.diaryContents) {
        diaryState?.diaryContents ?: return@LaunchedEffect
        if (contentsTextState.text.toString() != diaryState.diaryContents) {
            contentsTextState.setTextAndPlaceCursorAtEnd(diaryState.diaryContents)
        }
    }

    LaunchedEffect(diaryState?.diaryTag) {
        if (diaryState?.diaryTag.isNullOrEmpty()) return@LaunchedEffect
        if (hashTagList == diaryState.diaryTag) return@LaunchedEffect
        hashTagList.clear()
        hashTagList.addAll(diaryState.diaryTag)
    }

    //Keyboard 노출에 따른 Bottom Size
    val imeBottom = rememberImeBottomSize()
    LaunchedEffect(imeBottom) {
        if (imeBottom > 0) scrollState.scrollBy(imeBottom.toFloat())
        else focusManger.clearFocus()
    }

    Column(
        modifier = modifier.then(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .imePadding()
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier
                    .height(24.dp)
                    .noRippleClickable(onClick = onBackIconClick),
                painter = painterResource(R.drawable.icon_andgle_left),
                contentDescription = null,
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = if ((diaryState?.bookId ?: -1L) == -1L) "새 일기 쓰기" else "수정 하기",
                style = MaterialTheme.typography.titleMedium
            )
        }
        Row(
            modifier = Modifier
                .clickable(onClick = onBookTitleClick)
                .align(Alignment.End)
                .padding(end = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = selectDiary?.title ?: "",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Black33Color
                )
            )
            Spacer(Modifier.width(5.dp))
            Image(
                modifier = Modifier.height(16.dp),
                painter = painterResource(R.drawable.icon_drop_down),
                colorFilter = ColorFilter.tint(Black33Color),
                contentDescription = null
            )
        }
        Spacer(Modifier.height(15.dp))
        EditorDateItem(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            writeDate = writeDate,
            isCanChangeDate = isCanChangeDate,
            onShowCalendar = {
                onChangeCalendar()
            }
        )   // 날짜 View
        Spacer(Modifier.height(20.dp))
        CardPreviewItem(
            imageUrl = diaryState?.imageUrl,
            diaryContents = contentsTextState.text.toString(),
            diaryTag = diaryState?.diaryTag ?: emptyList(),
            onChangeImage = onShowImagePicker
        )   // 일기 미리보기
        Spacer(Modifier.height(10.dp))
        Text(
            modifier = Modifier.padding(start = 20.dp),
            text = "오늘 날의 기분은?",
            style = MaterialTheme.typography.labelSmall.copy(
                color = Black33Color
            )
        )
        Spacer(Modifier.height(5.dp))
        Image(
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.CenterHorizontally)
                .background(
                    color = if ((diaryState?.diaryMood ?: -1) == -1) BlackF4Color else TransParents,
                    shape = CircleShape
                )
                .clickable(onClick = {
                    isShowFeelSheet = true
                }),
            painter = if ((diaryState?.diaryMood ?: -1) == -1)
                painterResource(R.drawable.icon_select_feel)
            else painterResource(FeelData.feelList[diaryState?.diaryMood!!]),
            colorFilter = if ((diaryState?.diaryMood ?: -1) == -1) ColorFilter.tint(Black88Color)
            else null,
            contentDescription = null
        )
        Spacer(Modifier.height(20.dp))
        EditorItem(
            modifier = Modifier.padding(horizontal = 20.dp),
            textFieldState = contentsTextState,
            hashList = diaryState?.diaryTag ?: emptyList(),
            onChangeDiaryText = onChangeDiaryText,
            onChangeTagItem = onChangeHashTag,
        )   // 일기 작성
        Spacer(Modifier.height(20.dp))
        TextButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            contentsPadding = PaddingValues(vertical = 15.dp),
            text = "작성 완료",
            onClick = onSaveDairy
        )
    }

    if (isShowFeelSheet) {
        FeelBottomSheet(
            selectIndex = diaryState?.diaryMood ?: -1,
            onItemClick = onChangeFeel,
            onDismissRequest = {
                isShowFeelSheet = false
            }
        )
    }
}
