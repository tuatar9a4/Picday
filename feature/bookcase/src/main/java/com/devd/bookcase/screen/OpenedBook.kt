package com.devd.bookcase.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.devd.bookcase.BookcaseInterface
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.Black33Color
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.BlackOpacity40Color
import com.devd.commonsystem.theme.TransParents
import com.devd.commonsystem.utils.dropShadow
import com.devd.commonsystem.utils.noRippleClickable
import com.devd.commonsystem.utils.rememberImageUrl
import com.devd.model.local.DiaryBookInfo
import com.devd.model.local.DiaryInfo
import com.devd.model.local.DiaryPhaseType
import kotlinx.coroutines.delay

@Preview
@Composable
fun OpendBOokPreview() {
    val targetItem: DiaryBookInfo = DiaryBookInfo(
        bookId = 4857,
        bookColor = 5910,
        bookImage = "suscipiantur",
        title = "perpetua",
        description = "vel",
        bookPhaseType = DiaryPhaseType.MOON,
        continueWriteCount = 1960,
        createDate = 7709,
        monthWritePercent = 2.3f,
        isMajor = false

    )
    val bookWidth = LocalWindowInfo.current.containerDpSize.width - 60.dp
    val isOpened = true
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 35.dp, vertical = 20.dp)
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterStart),
                text = targetItem.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Black33Color
                )
            )
            Image(
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.CenterEnd),
                painter = painterResource(R.drawable.icon_more),
                contentDescription = null
            )
        }
        Box(
            modifier = Modifier
                .noRippleClickable(onClick = { })
                .padding(end = 21.dp, top = 76.dp)
                .background(
                    color = BlackOpacity40Color,
                    shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp)
                )
                .dropShadow(
                    shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp),
                    color = BlackColor,
                    blur = 4.dp, offsetX = 1.dp, offsetY = 2.dp
                )
                .padding(start = 13.dp),
        ) {
            OpenableBook(
                modifier = Modifier
                    .background(
                        color = TransParents,
                        shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp)
                    ),
                bookSize = IntSize(
                    bookWidth.value.toInt(),
                    (bookWidth.value * 17 / 9f).toInt()
                ),
                diaryList = listOf(
                    DiaryInfo(
                        diaryId = 3709,
                        diaryBookId = 6393,
                        content = "turpis",
                        mood = 3412,
                        weather = 4676,
                        createdAt = 8076,
                        updatedAt = 7061,
                        imageUrlList = listOf(),
                        tagList = listOf("231", "124", "24")
                    )
                ),
                rotation = if (isOpened) -180f else 0f,
                bookImage = targetItem.bookImage?.rememberImageUrl(),
            )
        }
    }
}

@Composable
fun SharedTransitionScope.OpenedBook(
    selectBook: DiaryBookInfo?,
    diaryList: List<DiaryInfo> = emptyList(),
    state: PagerState,
    layerMap: MutableMap<Int, GraphicsLayer>,
    bookClickAction: (BookcaseInterface) -> Unit = {},
    onDiaryMoreClick: (GraphicsLayer?) -> Unit,
    onBackClick: () -> Unit,
) {
    val bookWidth = LocalWindowInfo.current.containerDpSize.width - 60.dp
    var isOpened by remember() { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isOpened) -180f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "FlipAnimation"
    )

    LaunchedEffect(selectBook) {
        if (selectBook != null) {
            delay(200)
            isOpened = true
        }
    }

    BackHandler() {
        if (!isOpened) onBackClick()
        else isOpened = false

    }

    LaunchedEffect(rotation) {
        if (!isOpened && rotation > -60f && rotation != 0f) {
            bookClickAction(BookcaseInterface.OnColesDiaryBook)
        }
    }


    AnimatedContent(
        targetState = selectBook,
    ) { targetItem ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopStart
        ) {
            if (targetItem != null) {
                Box(
                    modifier = Modifier
                        .noRippleClickable(onClick = { isOpened = false })
                        .padding(end = 10.dp, top = 76.dp)
                        .background(
                            color = BlackOpacity40Color,
                            shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp)
                        )
                        .dropShadow(
                            shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp),
                            color = BlackColor,
                            blur = 4.dp, offsetX = 1.dp, offsetY = 2.dp
                        )
                        .padding(start = 13.dp),
                ) {
                    OpenableBook(
                        modifier = Modifier
                            .background(
                                color = TransParents,
                                shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp)
                            )
                            .sharedBounds(
                                sharedContentState = rememberSharedContentState(key = "book_${targetItem.bookId}"),
                                animatedVisibilityScope = this@AnimatedContent,
                            ),
                        bookSize = IntSize(
                            bookWidth.value.toInt(),
                            (bookWidth.value * 17 / 9f).toInt()
                        ),
                        state = state,
                        diaryList = diaryList,
                        layerMap = layerMap,
                        rotation = rotation,
                        bookImage = targetItem.bookImage?.rememberImageUrl(),
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 45.dp, vertical = 20.dp)
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = targetItem.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Black33Color
                        )
                    )
                    Image(
                        modifier = Modifier
                            .size(36.dp)
                            .noRippleClickable(onClick = {
                                onDiaryMoreClick(layerMap[state.currentPage])
                            })
                            .align(Alignment.CenterEnd),
                        painter = painterResource(R.drawable.icon_more),
                        contentDescription = null
                    )
                }
            }
        }
    }
}