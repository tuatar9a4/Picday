package com.devd.bookcase.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.BlackOpacity90Color
import com.devd.model.local.DiaryInfo


@Preview
@Composable
fun OpenableBookPreview() {
    Box(
        modifier = Modifier
            .graphicsLayer(clip = false)
            .width(500.dp)
            .height(600.dp)
            .background(BlackColor),
        contentAlignment = Alignment.Center,
    ) {
        OpenableBook(
            modifier = Modifier,
            rotation = -20f,
            bookSize = IntSize(200, 300),
            bookImage = "@3"
        )

    }
}

@Composable
fun OpenableBook(
    modifier: Modifier,
    bookSize: IntSize,
    diaryList: List<DiaryInfo> = emptyList(),
    rotation: Float = 0f,
    bookImage: String?
) {
    val bookWidth = bookSize.width.dp
    val height = bookSize.height.dp
    val state = rememberPagerState(0) { diaryList.size }

    Box(
        modifier = modifier.then(
            Modifier
                .width(bookWidth)
                .height(height)
        )
    ) {
        //본 페이지
        HorizontalPager(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp))
                .background(Color.White)
                .padding(20.dp),
            state = state
        ) { page ->
            DiaryCardScreen(diaryInfo = diaryList[page])
        }
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .padding(vertical = 1.dp)
                .width(8.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,       // 왼쪽 끝: 투명
                            BlackOpacity90Color,      // 중앙: 진하게
                            Color.Transparent        // 오른쪽 끝: 투명
                        )
                    )
                )
        )
        BookCover(
            modifier = Modifier.graphicsLayer {
                rotationY = rotation
                transformOrigin = TransformOrigin(0f, 0.5f)
                cameraDistance = 12f * density
            },
            bookSize = bookSize,
            coverImage = if (rotation < -90f) null else bookImage,
            isOpen = rotation < -90f
        )
    }
}