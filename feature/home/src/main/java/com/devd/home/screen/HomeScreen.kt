package com.devd.home.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.AccentOpacity40Color
import com.devd.commonsystem.theme.PrimaryColor
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.ui.Toolbar
import com.devd.commonsystem.ui.dialog.ShowMessageDialog
import com.devd.commonsystem.ui.loading.LoadingDialog
import com.devd.home.HomeViewModel
import com.devd.model.local.DiaryBookInfo
import timber.log.Timber


@Composable
fun HomeScreenRoute(
    modifier: Modifier = Modifier,
    onEditorMove: (uri: String) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {

    val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        uri?.let { onEditorMove(uri.toString()) }
    }

    LaunchedEffect(Unit) {
        Timber.d("LaunchEffect 이거 한번 만 호출되는거 맞지..?")
        viewModel.fetchMainDiaryBook()
    }

    HomeScreen(
        modifier = modifier,
        bookInfo = viewModel.bookInfo.value,
        photoPickerLauncher = pickMedia
//        onEditorClick = onEditorClick
    )
    viewModel.messageDialog.value?.ShowMessageDialog(onLeftButtonClick = viewModel::dismissDialog)
    viewModel.isLoading.value.LoadingDialog()
}

@Preview
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    bookInfo: DiaryBookInfo? = null,
    photoPickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>? = null,
    onEditorClick: () -> Unit = {},
) {
    Box(
        modifier = modifier.then(
            Modifier
                .fillMaxSize()
                .background(color = PrimaryColor)
        )
    ) {
        Column() {
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
                diaryTitle = bookInfo?.title ?: "",
                diaryDescription = bookInfo?.description ?: "",
                diaryMonthPercent = bookInfo?.monthWritePercent ?: 0f,
            )   // 일기장 카드
            Spacer(Modifier.height(20.dp))
            YearCategory(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                year = 2021,
                onClick = { year -> }
            )   // 년도 선택 스크린
            Spacer(Modifier.height(15.dp))
            DiaryListScreen(
                modifier = Modifier
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
                onClick = {
                    photoPickerLauncher?.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                },
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
                onClick = onEditorClick,
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