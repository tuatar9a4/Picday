package com.devd.diary

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.Black33Color
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.BlackF4Color
import com.devd.commonsystem.theme.BlackOpacity40Color
import com.devd.commonsystem.theme.RedColor
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.ui.Toolbar
import com.devd.commonsystem.ui.dialog.OptionBottomSheet
import com.devd.commonsystem.ui.dialog.ShowMessageDialog
import com.devd.commonsystem.ui.loading.LoadingDialog
import com.devd.commonsystem.utils.FeelData
import com.devd.diary.screen.DiaryContentsScreen
import com.devd.diary.screen.DiaryImageScreen
import com.devd.model.local.DiaryInfo
import com.devd.model.local.SheetItem

@Composable
fun DiaryListScreenRoute(
    modifier: Modifier = Modifier,
    changId: Long? = null,
    viewModel: DiaryListViewModel = hiltViewModel(),
    navigateEditPage: (String?, Long, Long?) -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.diaryListUiState.collectAsState()

    LaunchedEffect(uiState.diaryList) {
        if (uiState.diaryList.isEmpty()) onBackClick()
    }

    LaunchedEffect(changId) {
        changId?.let { viewModel.updateDiaryItem(changId) }
    }

    DiaryListScreen(
        modifier = modifier,
        diaryList = uiState.diaryList,
        startPos = uiState.startPos,
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
    onEditClick: (String?, Long, Long?) -> Unit,
    onBackClick: () -> Unit,
) {

    /* ImageList */
    val pagerState = rememberPagerState(pageCount = { diaryList.size }, initialPage = startPos)

    /* ContentsInfo */
    val currentDiary = diaryList.getOrNull(pagerState.currentPage)

    var isShowOptionSheet by remember { mutableStateOf(false) }


    Column(
        modifier = modifier.then(
            Modifier
                .fillMaxSize()
                .background(BlackColor)
        )
    ) {
        Toolbar(
            modifier = Modifier
                .fillMaxWidth()
                .background(WhiteColor),
            leftButtons = {
                Image(
                    modifier = Modifier.clickable(onClick = onBackClick),
                    painter = painterResource(R.drawable.icon_andgle_left),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Black33Color)
                )
            },
            rightButtons = {
                Image(
                    modifier = Modifier.clickable(onClick = {
                        isShowOptionSheet = true
                    }),
                    painter = painterResource(R.drawable.icon_more),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Black33Color)
                )
            }
        )
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.weight(1f),
            ) {
                DiaryImageScreen(
                    modifier = Modifier.fillMaxSize(),
                    pagerState = pagerState,
                    imageList = diaryList.map { it.imageUrlList.firstOrNull() },
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 20.dp, top = 10.dp)
                        .background(BlackOpacity40Color, CircleShape)
                        .border(1.dp, BlackF4Color, CircleShape)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    text = "${pagerState.currentPage+1}/${diaryList.size}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = WhiteColor
                    )
                )
            }
            DiaryContentsScreen(
                modifier = Modifier,
                diaryDate = currentDiary?.getDateStr("yyyy.MM.dd HH:mm") ?: "",
                diaryContents = currentDiary?.content ?: "",
                diaryFeel = FeelData.feelList.getOrNull(currentDiary?.mood ?: 0),
                diaryTagList = currentDiary?.tagList ?: emptyList()
            )   // 하단 컨텐츠
        }
    }

    if (isShowOptionSheet) {
        OptionBottomSheet(
            title = "",
            items = listOf(
                SheetItem(
                    itemIcon = R.drawable.icon_pencil,
                    id = "0",
                    text = "일기 수정"
                ),
                SheetItem(
                    itemIcon = R.drawable.icon_share,
                    id = "1",
                    text = "공유하기"
                ),
                SheetItem(
                    itemIcon = R.drawable.icon_delete_trash,
                    id = "2",
                    itemColor = RedColor,
                    text = "삭제"
                )
            ),
            onItemSelected = {
                isShowOptionSheet = false
                when (it.id) {
                    "0" -> {
                        val diaryInfo = diaryList[pagerState.currentPage]
                        onEditClick(
                            diaryInfo.imageUrlList.firstOrNull(),
                            diaryInfo.diaryBookId,
                            diaryInfo.diaryId
                        )
                    }

                    "1" -> {

                    }

                    "2" -> {
                        onDeleteClick(pagerState.currentPage)
                    }
                }
            },
            onDismissRequest = {
                isShowOptionSheet = false
            }
        )
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
        onEditClick = { _, _, _ -> },
        onDeleteClick = {},
        onBackClick = {}
    )
}
