package com.devd.diary

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.BlackOpacity40Color
import com.devd.commonsystem.theme.OneDayTypography
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.ui.dialog.ShowMessageDialog
import com.devd.commonsystem.ui.loading.LoadingDialog
import com.devd.diary.screen.DiaryContentsScreen
import com.devd.diary.screen.DiaryImageScreen
import com.devd.model.local.DiaryInfo
import timber.log.Timber

@Composable
fun DiaryListScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: DiaryListViewModel = hiltViewModel(),
    initList: List<DiaryInfo>,
    startPos: Int = 0,
    navigateEditPage: () -> Unit,
    onBackClick: () -> Unit
) {
    LaunchedEffect(Unit) { viewModel.setInitDiaryList(initList) }

    val uiState by viewModel.diaryListUiState.collectAsState()
    LaunchedEffect(uiState.isDiaryList) {
        Timber.d("CheckDiaryList => ${uiState.isDiaryList}")
        if (uiState.isDiaryList.isEmpty()) onBackClick()
    }
    DiaryListScreen(
        modifier = modifier,
        diaryList = uiState.isDiaryList,
        startPos = startPos,
        onDeleteClick = viewModel::showAskDeleteDiary,
        onEditClick = navigateEditPage,
        onBackClick = onBackClick
    )

    uiState.isLoading.LoadingDialog()
    uiState.popupCode.ShowMessageDialogWithCode(
        onDismiss = viewModel::dismissMessageDialog
    )
}

@Composable
private fun PopupCode.ShowMessageDialogWithCode(
    onDismiss: () -> Unit
) {
    return when (this) {
        PopupCode.NONE -> Unit
        is PopupCode.NotFoundItem ->
            (stringResource(R.string.not_found_delete_diary)).ShowMessageDialog(onDismiss)

        is PopupCode.AskDeleteDiary ->
            (stringResource(R.string.ask_delete_diary_message))
                .ShowMessageDialog(
                    onRightButtonClick = this.confirmCallback,
                    leftButtonMessage = R.string.cancel,
                    onLeftButtonClick = onDismiss
                )
    }
}

@Composable
fun DiaryListScreen(
    modifier: Modifier = Modifier,
    diaryList: List<DiaryInfo>,
    startPos: Int = 0,
    onDeleteClick: (id: Int) -> Unit,
    onEditClick: () -> Unit,
    onBackClick: () -> Unit,
) {

    /* ImageList */
    val pagerState = rememberPagerState(pageCount = { diaryList.size }, initialPage = startPos)

    /* ContentsInfo */
    val dateStr = remember { mutableStateOf("") }
    val contentsStr = remember { mutableStateOf("") }
    val hashTag = remember { mutableStateOf(emptyList<String>()) }

    fun changeDiaryContents(pos: Int) {
        if (diaryList.isEmpty()) return
        val selectPageInfo = diaryList[pos]
        dateStr.value = selectPageInfo.cratedDateStr("yyyy.MM.dd HH:mm")
        contentsStr.value = selectPageInfo.content
        hashTag.value = selectPageInfo.tagList
    }

    Box(
        modifier = modifier.then(
            Modifier
                .fillMaxSize()
                .background(BlackColor)
        )
    ) {
        DiaryImageScreen(
            pagerState = pagerState,
            imageList = diaryList.map { it.imageUrlList.firstOrNull() },
        ) { changedPos ->
            changeDiaryContents(changedPos)
        }   //이미지 Screen
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BlackOpacity40Color)
                .padding(horizontal = 15.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row() {
                Image(
                    modifier = Modifier
                        .size(32.dp)
                        .padding(4.dp)
                        .clickable(onClick = onBackClick),
                    painter = painterResource(R.drawable.icon_back_arrow),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(WhiteColor)
                )
                Spacer(Modifier.width(37.dp))
            }
            Text(
                text = "BookName",
                style = OneDayTypography.bodyMedium.copy(
                    color = WhiteColor
                )
            )
            Row() {
                Image(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable(onClick = { onDeleteClick(pagerState.currentPage) })
                        .padding(4.dp),
                    painter = painterResource(R.drawable.icon_delete_trash),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(WhiteColor)
                )
                Spacer(Modifier.width(5.dp))
                Image(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable(onClick = onEditClick)
                        .padding(4.dp),
                    painter = painterResource(R.drawable.icon_pencil),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(WhiteColor)
                )
            }
        }
        DiaryContentsScreen(
            modifier = Modifier.align(Alignment.BottomCenter),
            diaryDate = dateStr.value,
            diaryContents = contentsStr.value,
            diaryTagList = hashTag.value
        )   // 하단 컨텐츠
    }

}


@Preview
@Composable
fun DiaryListScreenPreview() {
    DiaryListScreen(
        diaryList = listOf(
            DiaryInfo(
                diaryId = 8195,
                diaryBookId = 8186,
                content = "tacimates",
                mood = 3871,
                weather = 5768,
                createdAt = 7101,
                updatedAt = 9280,
                imageUrlList = listOf(),
                tagList = listOf("Tag?")

            ),
        ),
        onEditClick = {},
        onDeleteClick = {},
        onBackClick = {}
    )
}
