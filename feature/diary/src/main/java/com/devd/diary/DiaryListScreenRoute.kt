package com.devd.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.BlackOpacity40Color
import com.devd.commonsystem.ui.Toolbar
import com.devd.commonsystem.ui.loading.LoadingDialog
import com.devd.diary.screen.DiaryContentsScreen
import com.devd.diary.screen.DiaryImageScreen
import com.devd.model.local.DiaryInfo

@Composable
fun DiaryListScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: DiaryListViewModel = hiltViewModel(),
    initList: List<DiaryInfo>,
    startPos: Int = 0,
    onBackClick: () -> Unit
) {
    viewModel.setInitDiaryList(initList)

    val uiState by viewModel.diaryListUiState.collectAsState()

    DiaryListScreen(
        modifier = modifier,
        diaryList = uiState.isDiaryList,
        startPos = startPos,
        onBackClick = onBackClick
    )

    uiState.isLoading.LoadingDialog()

}

@Composable
fun DiaryListScreen(
    modifier: Modifier = Modifier,
    diaryList: List<DiaryInfo>,
    startPos: Int = 0,
    onBackClick: () -> Unit,
) {

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
            imageList = diaryList.map { it.imageUrlList.firstOrNull() },
            startPos = startPos
        ) { changedPos ->
            changeDiaryContents(changedPos)
        }   //이미지 Screen

        Toolbar(
            modifier = Modifier
                .background(BlackOpacity40Color),
            title = "",
            leftButtonIcon = R.drawable.icon_back_arrow,
            leftButtonClick = onBackClick,
            rightButtonIcon = R.drawable.icon_hamburger,
            rightButtonClick = {},
            useWhitIcon = true
        )

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
        onBackClick = {}
    )
}
