package com.devd.home.screen

import android.app.Activity.RESULT_CANCELED
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.AccentOpacity40Color
import com.devd.commonsystem.theme.PrimaryColor
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.ui.Toolbar
import com.devd.commonsystem.ui.calendar.CustomDatePickerDialog
import com.devd.commonsystem.ui.dialog.ShowMessageDialog
import com.devd.commonsystem.ui.loading.LoadingDialog
import com.devd.commonsystem.utils.centerItemIndex
import com.devd.commonsystem.utils.isCurrentMonth
import com.devd.home.HomeUiState
import com.devd.home.HomeViewModel
import com.devd.permission.Consts
import com.devd.permission.IPermissionHandler
import com.devd.permission.rememberPermissionHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File


@Composable
fun HomeScreenRoute(
    modifier: Modifier = Modifier,
    onEditorMove: (imageUrl: String, bookId: Long, diaryId: Long?) -> Unit = { _, _, _ -> },
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.homeUiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val permissionHandler: IPermissionHandler = rememberPermissionHandler()
    var cameraUri by remember { mutableStateOf<Uri?>(null) }
    val scope = rememberCoroutineScope()

    val diaryState = rememberLazyListState()

    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { uri ->
            if (uri.resultCode == RESULT_CANCELED) return@rememberLauncherForActivityResult
            Timber.d("Check Uri => ${uri.data?.data}")
            Timber.d("cameraUri => ${cameraUri}")
            (uri.data?.data ?: cameraUri)?.let {
                val selectIndex = diaryState.centerItemIndex() ?: -1
                val selectDiary = uiState.diaryList.getOrNull(selectIndex)
                onEditorMove.invoke(
                    it.toString(),
                    uiState.bookInfo?.bookId ?: 0,
                    selectDiary?.diaryId?.takeIf { id -> id != -1L }
                )
            }
        }



    LaunchedEffect(Unit) {
        viewModel.fetchMainDiaryBook()
    }

    HomeScreen(
        modifier = modifier,
        uiState = uiState,
        diaryState = diaryState,
        onClickDate = viewModel::showCalendarDialog,
        onEditorClick = {
            scope.launch {
                val grant = permissionHandler.requestPermissionIfNeeded(Consts.CAMERA_PERMISSION)
                if (grant.any { !it.value }) {
                    viewModel.showMessageDialog(R.string.need_camera_permission)
                } else {
                    cameraUri = createCameraUri(context)
                    pickMedia.launch(createImageChooserIntent(cameraUri!!))
                }
            }
        }
    )
    uiState.dialogMessage?.ShowMessageDialog(onLeftButtonClick = viewModel::dismissDialog)
    uiState.isLoading.LoadingDialog()
    if (uiState.isShowCalendar) {
        CustomDatePickerDialog(
            title = "검색할 날짜를 선택해주세요",
            initDateMillis = uiState.searchDate.toInstant().toEpochMilli(),
            onSelectDate = viewModel::changeSearchMonth,
            onClickCancel = viewModel::dismissCalendar
        )
    }
}

@Preview
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState = HomeUiState(),
    diaryState: LazyListState = rememberLazyListState(),
    onClickDate: () -> Unit = {},
    onEditorClick: () -> Unit = {},
) {
    Box(
        modifier = modifier.then(
            Modifier
                .fillMaxSize()
                .background(color = PrimaryColor)
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Toolbar(
                modifier = Modifier.background(color = PrimaryColor),
                title = "",
                leftButtonIcon = R.drawable.icon_library,
                leftButtonClick = {},
                rightButtonIcon = R.drawable.icon_setting,
                rightButtonClick = {})
            Spacer(Modifier.height(20.dp))
            BookCardScreen(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                diaryTitle = uiState.bookInfo?.title ?: "",
                diaryDescription = uiState.bookInfo?.description ?: "",
                diaryMonthPercent = uiState.bookInfo?.monthWritePercent ?: 0f,
            )   // 일기장 카드
            Spacer(Modifier.height(20.dp))
            YearCategory(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                searchDate = uiState.searchDate,
                onClick = { time ->
                    onClickDate.invoke()
                }
            )   // 년도 선택 스크린
            Spacer(Modifier.height(15.dp))
            DiaryListScreen(
                modifier = Modifier,
                diaryState = diaryState,
                diaryList = uiState.diaryList,
                isCurrentMonth = uiState.searchDate.isCurrentMonth(),
                onAddCardClick = onEditorClick
            )   // DiaryList 스크린
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 40.dp)

        ) {
            FloatingActionButton(
                modifier = Modifier
                    .size(42.dp),
                shape = CircleShape,
                containerColor = AccentOpacity40Color,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
                onClick = onEditorClick
            ) {
                Image(
                    painter = painterResource(R.drawable.icon_pencil),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(WhiteColor)
                )
            }   // 일기장 작성 이동 버튼
            Spacer(Modifier.width(20.dp))
            FloatingActionButton(
                modifier = Modifier
                    .size(42.dp),
                shape = CircleShape,
                containerColor = AccentOpacity40Color,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
                onClick = { },
            ) {
                Image(
                    painter = painterResource(R.drawable.icon_calendar),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(WhiteColor)
                )
            }   // 달력 이동 번튼
            Spacer(Modifier.width(20.dp))
        }
    }
}


private fun createCameraUri(context: Context): Uri {
    val file = File(
        context.cacheDir,
        "camera_${System.currentTimeMillis()}.jpg"
    )
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}

private fun createImageChooserIntent(cameraUri: Uri): Intent {

    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
        putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)
        addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    }

    val galleryIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
        type = "image/*"
        addCategory(Intent.CATEGORY_OPENABLE)
    }

    return Intent.createChooser(
        galleryIntent,
        "이미지 선택"
    ).apply {
        putExtra(
            Intent.EXTRA_INITIAL_INTENTS,
            arrayOf(cameraIntent)
        )
    }
}