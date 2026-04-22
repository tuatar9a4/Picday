package com.devd.bookcase.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.devd.bookcase.BookcaseInterface
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
    val bookWidth = LocalWindowInfo.current.containerDpSize.width - 75.dp
    val isOpened = true
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .noRippleClickable(onClick = { })
                .padding(end = 21.dp)
                .background(
                    color = BlackOpacity40Color,
                    shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp)
                )
                .dropShadow(
                    shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp),
                    color = BlackColor,
                    blur = 4.dp, offsetX = 1.dp, offsetY = 2.dp
                )
                .padding(all = 13.dp),
        ) {
            OpenableBook(
                modifier = Modifier
                    .background(
                        color = TransParents,
                        shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp)
                    ),
                bookSize = IntSize(
                    bookWidth.value.toInt(),
                    (bookWidth.value * 16 / 9f).toInt()
                ),
                diaryList = emptyList(),
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
    bookClickAction: (BookcaseInterface) -> Unit = {},
    onBackClick: () -> Unit,
) {
    val bookWidth = LocalWindowInfo.current.containerDpSize.width - 100.dp
    val isOpened = remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isOpened.value) -180f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "FlipAnimation"
    )

    LaunchedEffect(selectBook) {
        if (selectBook != null) {
            delay(200)
            isOpened.value = true
        }
    }

    BackHandler() {
        if (!isOpened.value) onBackClick()
        else isOpened.value = false

    }

    LaunchedEffect(rotation) {
        if (!isOpened.value && rotation > -60f && rotation != 0f) {
            bookClickAction(BookcaseInterface.OnColesDiaryBook)
        }
    }


    AnimatedContent(
        targetState = selectBook,
    ) { targetItem ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (targetItem != null) {
                Box(
                    modifier = Modifier
                        .noRippleClickable(onClick = { isOpened.value = false })
                        .fillMaxSize()
                        .background(color = BlackOpacity40Color),
                    contentAlignment = Alignment.Center
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
                            (bookWidth.value * 16 / 9f).toInt()
                        ),
                        diaryList = diaryList,
                        rotation = rotation,
                        bookImage = targetItem.bookImage?.rememberImageUrl(),
                    )
                }
            }
        }
    }
}