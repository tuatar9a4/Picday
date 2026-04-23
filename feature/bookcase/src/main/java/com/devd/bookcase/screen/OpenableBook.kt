package com.devd.bookcase.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.Black88Color
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.BlackOpacity90Color
import com.devd.commonsystem.theme.SemiVioletColor
import com.devd.model.local.DiaryInfo


@Preview
@Composable
fun OpenableBookPreview() {
    Box(
        modifier = Modifier
            .graphicsLayer(clip = false)
            .fillMaxSize()
            .padding(horizontal = 50.dp)
            .background(BlackColor),
        contentAlignment = Alignment.CenterStart,
    ) {
        OpenableBook(
            modifier = Modifier,
            rotation = -0f,
            diaryList = listOf(
                DiaryInfo(
                    diaryId = 3765,
                    diaryBookId = 9898,
                    content = "mollis",
                    mood = 2340,
                    weather = 2418,
                    createdAt = 0,
                    updatedAt = 2899,
                    imageUrlList = listOf(),
                    tagList = listOf()
                )
            ),
            bookSize = IntSize(900, 1600),
            bookImage = "@3"
        )

    }
}

@Composable
fun OpenableBook(
    modifier: Modifier,
    bookSize: IntSize,
    diaryList: List<DiaryInfo> = emptyList(),
    state: PagerState = rememberPagerState(0) { diaryList.size },
    layerMap : MutableMap<Int, GraphicsLayer> = mutableMapOf(),
    rotation: Float = 0f,
    bookImage: String?
) {
    val bookWidth = bookSize.width.dp
    val height = bookSize.height.dp
    var diaryDate by remember { mutableStateOf<String?>(null) }
    var feelIcon by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(diaryList, state.currentPage) {
        val diaryItem = diaryList.getOrNull(state.currentPage) ?: return@LaunchedEffect
        diaryDate = diaryItem.getDateStr("YYYY.MM.dd")
        feelIcon = 32
    }

    Box(
        modifier = modifier.then(
            Modifier
                .width(bookWidth)
                .height(height)
        )
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp)),
            painter = painterResource(R.drawable.img_diary_bg_01),
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
        //본 페이지
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp))
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                modifier = Modifier.padding(start = 25.dp),
                text = diaryDate ?: "",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Black88Color
                )
            )
            Box(
                modifier = Modifier
            ) {
                HorizontalPager(
                    modifier = Modifier.padding(
                        start = 25.dp,
                        end = 35.dp,
                        top = 30.dp,
                        bottom = 45.dp
                    ),
                    state = state
                ) { page ->
                    // 2. 각 페이지마다 고유한 레이어 생성
                    val layer = rememberGraphicsLayer()

                    // 3. 생성된 레이어를 부모의 Map에 등록 (Key는 페이지 번호)
                    DisposableEffect(layer) {
                        layerMap[page] = layer
                        onDispose { layerMap.remove(page) } // 페이지 파괴 시 삭제
                    }
                    DiaryCardScreen(
                        graphicsLayer = layer,
                        diaryInfo = diaryList[page]
                    )
                }
                feelIcon?.let {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .background(SemiVioletColor)
                            .padding(vertical = 4.dp, horizontal = 24.dp)
                    ) {
                        Image(
                            modifier = Modifier.size(36.dp),
                            painter = painterResource(R.drawable.icon_library),
                            contentDescription = null
                        )
                    }
                }
            }
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