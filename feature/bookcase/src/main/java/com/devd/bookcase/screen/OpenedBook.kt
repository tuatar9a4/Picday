package com.devd.bookcase.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.devd.bookcase.BookcaseInterface
import com.devd.commonsystem.theme.BlackOpacity40Color
import com.devd.commonsystem.utils.rememberImageUrl
import com.devd.model.local.DiaryBookInfo
import com.devd.model.local.DiaryInfo
import kotlinx.coroutines.delay

@Composable
fun SharedTransitionScope.OpenedBook(
    selectBook: DiaryBookInfo?,
    diaryList: List<DiaryInfo> = emptyList(),
    bookClickAction: (BookcaseInterface) -> Unit = {},
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
            delay(100)
            isOpened.value = true
        }
    }

    BackHandler() {
        isOpened.value = false
    }

    LaunchedEffect(rotation) {
        if (!isOpened.value && rotation > -60f && rotation != 0f) {
            bookClickAction(BookcaseInterface.OnColesDiaryBook)
        }
    }


    AnimatedContent(
        targetState = selectBook,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
    ) { targetItem ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (targetItem != null) {
                Box(
                    modifier = Modifier
                        .clickable(onClick = { isOpened.value = false })
                        .fillMaxSize()
                        .background(color = BlackOpacity40Color),
                    contentAlignment = Alignment.Center
                ) {
                    OpenableBook(
                        modifier = Modifier
                            .sharedBounds(
                                sharedContentState = rememberSharedContentState(key = "book_${targetItem.bookId}"),
                                animatedVisibilityScope = this@AnimatedContent,
                            ),
                        bookSize = IntSize(bookWidth.value.toInt(), (bookWidth.value * 16/9f).toInt()),
                        diaryList = diaryList,
                        rotation = rotation,
                        bookImage = targetItem.bookImage?.rememberImageUrl(),
                        onCloseBook = {
                            isOpened.value = false
                        }
                    )
                }
            }
        }
    }
}