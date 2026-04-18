package com.devd.editor.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.ui.calendar.CustomDatePickerDialog
import com.devd.commonsystem.ui.calendar.RangeType
import com.devd.commonsystem.ui.cropImageDialog.ShowCropDialog
import com.devd.commonsystem.ui.dialog.OptionBottomSheet
import com.devd.commonsystem.ui.dialog.ShowImagePicker
import com.devd.commonsystem.ui.dialog.ShowMessageDialog
import com.devd.commonsystem.ui.loading.LoadingDialog
import com.devd.commonsystem.utils.noRippleClickable
import com.devd.commonsystem.utils.rememberImagePicker
import com.devd.commonsystem.utils.uriToFile
import com.devd.editor.EditorViewModel
import com.devd.editor.data.ASK_SAVE
import com.devd.editor.data.DiaryInfoState
import com.devd.editor.data.Local
import com.devd.editor.data.SAVE_SUCCESS
import com.devd.editor.data.SAVE_UPDATE
import com.devd.model.local.DiaryBookInfo
import com.devd.model.local.SheetItem

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

    /* Resolvation Move Back */
    LaunchedEffect(viewModel.shouldBackPage) {
        if (viewModel.shouldBackPage) {
            onBackIconClick.invoke(uiState.diaryInfo.diaryId)
            viewModel.shouldBackPage = false
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
                    val file =
                        (uiState.diaryInfo.imageUrl as? Local)?.uri?.let { context.uriToFile(it) }
                    viewModel.uploadImageToBuket(fileUrl = file)
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
    onBackIconClick: () -> Unit = {},
    onChangeCalendar: () -> Unit = {},
    onChangeDiaryText: (String) -> Unit = {},
    onChangeHashTag: (String?, String?) -> Unit = { _, _ -> }
) {

    val contentsTextState = rememberTextFieldState(diaryState?.diaryContents ?: "z")

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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                modifier = Modifier
                    .height(24.dp)
                    .noRippleClickable(onClick = onBackIconClick),
                painter = painterResource(R.drawable.icon_back_arrow),
                contentDescription = null,
            )
            Row(
                modifier = Modifier.clickable(onClick = onBookTitleClick),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = selectDiary?.title ?: "",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.width(5.dp))
                Image(
                    modifier = Modifier.height(16.dp),
                    painter = painterResource(R.drawable.icon_drop_down),
                    contentDescription = null
                )
            }
            Image(
                modifier = Modifier
                    .height(24.dp)
                    .noRippleClickable(onClick = onSaveDairy),
                painter = painterResource(R.drawable.icon_pencil),
                contentDescription = null,
            )
        }
        Spacer(Modifier.height(15.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            EditorDateItem(
                writeDate = writeDate,
                isCanChangeDate = isCanChangeDate,
                onShowCalendar = {
                    onChangeCalendar()
                }
            )   // 날짜 View
            Column(
                modifier = Modifier.padding(end = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Feel",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = BlackColor
                    )
                )
                Image(
                    modifier = Modifier
                        .border(width = 1.dp, color = BlackColor, shape = RoundedCornerShape(5.dp))
                        .padding(5.dp),
                    painter = painterResource(R.drawable.icon_pencil),
                    contentDescription = null
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        CardPreviewItem(
            imageUrl = diaryState?.imageUrl,
            diaryContents = contentsTextState.text.toString(),
            diaryTag = diaryState?.diaryTag ?: emptyList(),
            onChangeImage = onShowImagePicker
        )   // 일기 미리보기
        Spacer(Modifier.height(10.dp))
        EditorItem(
            modifier = Modifier,
            textFieldState = contentsTextState,
            hashList = diaryState?.diaryTag ?: emptyList(),
            onChangeDiaryText = onChangeDiaryText,
            onChangeTagItem = onChangeHashTag,
        )   // 일기 작성
        Spacer(Modifier.height(20.dp))
    }

}
