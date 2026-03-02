package com.devd.editor.screen

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devd.commonsystem.R
import com.devd.commonsystem.ui.Toolbar
import com.devd.commonsystem.ui.calendar.CustomDatePickerDialog
import com.devd.commonsystem.ui.calendar.RangeType
import com.devd.commonsystem.ui.cropImageDialog.ShowCropDialog
import com.devd.commonsystem.ui.dialog.ShowImagePicker
import com.devd.commonsystem.ui.dialog.ShowMessageDialog
import com.devd.commonsystem.ui.loading.LoadingDialog
import com.devd.commonsystem.utils.rememberImagePicker
import com.devd.commonsystem.utils.uriToFile
import com.devd.editor.EditorViewModel
import com.devd.editor.data.ASK_SAVE
import com.devd.editor.data.DiaryInfoState
import com.devd.editor.data.Local
import com.devd.editor.data.SAVE_SUCCESS
import com.devd.editor.data.SAVE_UPDATE

@Composable
fun rememberImeBottomSize(): Int {
    val density = LocalDensity.current
    return WindowInsets.ime.getBottom(density)
}

@Composable
fun EditorScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: EditorViewModel = hiltViewModel(),
    currentTime: Long,
    diaryImage: String?,
    bookId: Long,
    diaryId: Long?,
    onBackIconClick: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.initSelectDate(currentTime)
        viewModel.initDiaryInfo(bookId, diaryId, diaryImage)
    }

    /* Resolvation Move Back */
    LaunchedEffect(viewModel.shouldBackPage) {
        if (viewModel.shouldBackPage) {
            onBackIconClick.invoke()
            viewModel.shouldBackPage = false
        }
    }

    /* UiState */
    val uiState by viewModel.editorUiState.collectAsStateWithLifecycle()

    /* MessageDialog */
    val messageDialog by viewModel.messageDialog.collectAsStateWithLifecycle(null)

    /* ImagePicker */
    var isShowPickerDialog by remember { mutableStateOf(false) }
    val imagePicker = rememberImagePicker { uri ->
        viewModel.changeCropImageDialog(uri)
    }

    /* CalendarDialog */
    val customDatePickerDialogState by viewModel.customDatePickerDialogState.collectAsState()

    val diaryInfoState by viewModel.diaryInfoState.collectAsState()
    EditorScreen(
        modifier = modifier,
        writeDate = customDatePickerDialogState.selectedDate,
        diaryState = diaryInfoState,
        onShowImagePicker = { isShowPickerDialog = true },
        onChangeDiaryText = viewModel::setDiaryText,
        onChangeHashTag = viewModel::changeHashTag,
        onSaveDairy = viewModel::showAskSavePopup,
        onBackIconClick = onBackIconClick,
        onChangeCalendar = viewModel::showDatePickerDialog,
    )
    if (customDatePickerDialogState.isShowDialog) {
        CustomDatePickerDialog(
            title = "일기 작성 날짜를 선택하세요",
            selectRange = RangeType.CURRENT_MONTH,
            initDateMillis = customDatePickerDialogState.selectedDate,
            onSelectDate = customDatePickerDialogState.onClickConfirm,
            onClickCancel = customDatePickerDialogState.onClickCancel
        )
    }

    isShowPickerDialog.ShowImagePicker(
        onCameraClick = imagePicker.launchCamera,
        onGalleryClick = imagePicker.launchGallery,
        onDismiss = { isShowPickerDialog = false }
    )   // ImagePicker
    uiState.isShowLoading.LoadingDialog()   // LoadingDialog TODO : MoveMainActivity로 옮기기
    uiState.imageUrlForCrop?.ShowCropDialog { cropFile ->
        viewModel.changeCropImageDialog(null)
        cropFile?.let { viewModel.updateImageUrl(it.toUri()) }
    }   // CropDialog
    messageDialog?.getMessage()
        ?.ShowMessageDialog(
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
                            (diaryInfoState.imageUrl as? Local)?.uri?.let { context.uriToFile(it) }
                        viewModel.uploadImageToBuket(fileUrl = file)
                    }

                    else -> {
                        viewModel.dismissMessageDialog()
                    }
                }
            }
        )   // MessageDialog
}

@Preview
@Composable
fun EditorScreen(
    modifier: Modifier = Modifier,
    writeDate: Long = System.currentTimeMillis(),
    diaryState: DiaryInfoState? = null,
    onShowImagePicker: () -> Unit = {},
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
        Toolbar(
            title = stringResource(R.string.editor_page_title),
            leftButtonIcon = R.drawable.icon_back_arrow,
            leftButtonClick = onBackIconClick,
            rightButtonIcon = R.drawable.icon_pencil,
            rightButtonClick = onSaveDairy
        )
        Spacer(Modifier.height(15.dp))
        EditorDateItem(
            writeDate = writeDate,
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
