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
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.devd.commonsystem.R
import com.devd.commonsystem.ui.Toolbar
import com.devd.commonsystem.ui.calendar.CustomDatePickerDialog
import com.devd.commonsystem.ui.dialog.ShowImagePicker
import com.devd.commonsystem.utils.rememberImagePicker
import com.devd.commonsystem.utils.uriToFile
import com.devd.editor.EditorViewModel
import kotlinx.coroutines.launch


@Composable
fun rememberImeVisible(): Boolean {
    return WindowInsets.ime.getBottom(LocalDensity.current) > 0
}

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
    diaryImage: String,
    bookId: Long,
    diaryId: Long?,
    onBackIconClick: () -> Unit
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) { viewModel.initSelectDate(currentTime) }
    val customDatePickerDialogState by viewModel.customDatePickerDialogState.collectAsState()

    EditorScreen(
        modifier = modifier,
        writeDate = customDatePickerDialogState.selectedDate,
        diaryImage = diaryImage,
        onSaveDairy = {
            scope.launch {
                val file = context.uriToFile(diaryImage.toUri())
                viewModel.uploadImageToBuket(fileUrl = file)
            }
        },
        onBackIconClick = onBackIconClick,
        onChangeCalendar = viewModel::showDatePickerDialog,
        onChangeDiaryText = viewModel::setDiaryText,
    )
    if (customDatePickerDialogState.isShowDialog) {
        CustomDatePickerDialog(
            title = "일기 작성 날짜를 선택하세요",
            initDateMillis = customDatePickerDialogState.selectedDate,
            onSelectDate = customDatePickerDialogState.onClickConfirm,
            onClickCancel = customDatePickerDialogState.onClickCancel
        )
    }
}

@Preview
@Composable
fun EditorScreen(
    modifier: Modifier = Modifier,
    writeDate: Long = System.currentTimeMillis(),
    diaryImage: String? = null,
    onSaveDairy: () -> Unit = {},
    onBackIconClick: () -> Unit = {},
    onChangeCalendar: () -> Unit = {},
    onChangeDiaryText: (String) -> Unit = {}
) {

    var imageUrl by remember { mutableStateOf(diaryImage?.toUri()) }
    var isShowPickerDialog by remember { mutableStateOf(false) }
    val imagePicker = rememberImagePicker { uri ->
        uri?.let { imageUrl = it }
    }


    val contentsTextState = rememberTextFieldState("")

    val hashTagList = remember { mutableStateListOf<String>() }
    val focusManger = LocalFocusManager.current
    val scrollState = rememberScrollState()

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
            rightButtonClick = onSaveDairy
        )
        Spacer(Modifier.height(15.dp))
        EditorDateItem(
            writeDate = writeDate, onShowCalendar = onChangeCalendar
        )   // 날짜 View
        Spacer(Modifier.height(20.dp))
        CardPreviewItem(
            imageUrl = imageUrl,
            diaryText = contentsTextState.text.toString(),
            diaryTag = hashTagList,
            onChangeImage = { isShowPickerDialog = true }
        )   // 일기 미리보기
        Spacer(Modifier.height(10.dp))
        EditorItem(
            modifier = Modifier,
            textFieldState = contentsTextState,
            onChangeDiaryText = onChangeDiaryText,
            hashList = hashTagList
        )   // 일기 작성
        Spacer(Modifier.height(20.dp))
    }

    isShowPickerDialog.ShowImagePicker(
        onCameraClick = imagePicker.launchCamera,
        onGalleryClick = imagePicker.launchGallery,
        onDismiss = { isShowPickerDialog = false }
    )

}
