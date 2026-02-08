package com.devd.home.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.BlackOpacity40Color
import com.devd.commonsystem.theme.OneDayTypography
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.utils.noRippleClickable
import com.devd.model.local.DiaryInfo
import kotlin.math.absoluteValue
import kotlin.math.max

@Composable
fun DiaryListScreen(
    modifier: Modifier = Modifier,
    isCurrentMonth: Boolean = true,
    diaryList: List<DiaryInfo> = emptyList(),
) {

    val configuration = LocalConfiguration.current
    val displayWidth = configuration.screenWidthDp.dp

    val diaryListState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = diaryListState)
    if (diaryList.isEmpty()) {
        AddDiaryCardScreen(
            modifier = modifier,
            onClick = {}
        )
    } else {
        LazyRow(
            modifier = modifier.then(Modifier.fillMaxWidth()),
            state = diaryListState,
            reverseLayout = true,
            flingBehavior = flingBehavior,
            contentPadding = PaddingValues(horizontal = (displayWidth / 2) - 130.dp),
            horizontalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            itemsIndexed(diaryList) { index, item ->
                val scale = calculateItemScale(diaryListState, index)
                if (index == 0 && isCurrentMonth && !item.isTodayItem) {
                    AddDiaryCardScreen(
                        onClick = {}
                    )
                }
                DiaryCardScreen(
                    modifier = Modifier.graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    },
                    diaryDate = item.createDay.toString(),
                    diaryTitle = item.content,
                    diaryTag = item.tagList,
                )
            }
        }
    }
}

@Composable
fun DiaryCardScreen(
    modifier: Modifier = Modifier,
    diaryDate: String,
    diaryTitle: String,
    diaryTag: List<String>,
    diaryImage: String? = null,
    diarySticker: String? = null,
) {
    Card(
        modifier = modifier.then(
            Modifier
                .width(260.dp)
                .background(color = WhiteColor)
                .aspectRatio(ratio = 9 / 16f)
        ),
        shape = RoundedCornerShape(20.dp),
    ) {
        Box(
            modifier = Modifier
                .background(color = WhiteColor)
                .fillMaxSize()
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp),
                text = diaryDate,
                style = OneDayTypography.titleMedium.copy(
                    color = BlackColor
                )
            )
            diaryImage?.let {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(R.drawable.icon_diary),
                    contentDescription = null
                )
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(BlackOpacity40Color)
                    .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 10.dp),

                ) {
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        minLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        text = diaryTitle,
                        style = OneDayTypography.bodySmall.copy(
                            color = WhiteColor
                        )
                    )
                    Spacer(Modifier.width(10.dp))
                    Image(
                        modifier = Modifier
                            .size(16.dp)
                            .rotate(180f),
                        painter = painterResource(R.drawable.icon_back_arrow),
                        colorFilter = ColorFilter.tint(WhiteColor),
                        contentDescription = null
                    )
                }
                Spacer(Modifier.height(5.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    diaryTag.forEach {
                        Text(
                            text = "#$it",
                            style = OneDayTypography.labelLarge.copy(
                                color = WhiteColor
                            )
                        )
                    }
                }
                diarySticker.let {
                    Spacer(Modifier.height(6.dp))
                    Image(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(R.drawable.icon_show_password_eye),
                        contentDescription = null
                    )
                }
            }
        }

    }
}

@Composable
fun AddDiaryCardScreen(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.then(
            Modifier
                .width(260.dp)
                .aspectRatio(ratio = 9 / 16f)
                .noRippleClickable(onClick = onClick)
        ),
        shape = RoundedCornerShape(20.dp),
    ) {
        Box(
            modifier = Modifier
                .background(color = WhiteColor)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.size(40.dp),
                    painter = painterResource(R.drawable.icon_pencil),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.size(20.dp))
                Text(
                    text = "오늘의 한 컷을 남겨주세요",
                    style = OneDayTypography.bodyLarge
                )

            }
        }
    }
}

@Preview
@Composable
fun DiaryCardScreenPreview() {
    DiaryCardScreen(
        diaryDate = "1",
        diaryTitle = "다이어리 타이틀",
        diaryTag = listOf("123", "321")
    )
}

@Preview
@Composable
fun DiaryListScreenPreview() {
    DiaryListScreen(
        diaryList = listOf(
            DiaryInfo(
                diaryId = 9704,
                diaryBookId = 4218,
                content = "ac",
                mood = 9955,
                weather = 2682,
                createdAt = 4071,
                updatedAt = 2301,
                imageUrlList = listOf(),
                tagList = listOf()
            ),
            DiaryInfo(
                diaryId = 12,
                diaryBookId = 4218,
                content = "Goof",
                mood = 9955,
                weather = 2682,
                createdAt = 4071,
                updatedAt = 2301,
                imageUrlList = listOf(),
                tagList = listOf()
            ),
            DiaryInfo(
                diaryId = 154,
                diaryBookId = 4218,
                content = "124124",
                mood = 9955,
                weather = 2682,
                createdAt = 4071,
                updatedAt = 2301,
                imageUrlList = listOf(),
                tagList = listOf()
            ),
            DiaryInfo(
                diaryId = 26,
                diaryBookId = 4218,
                content = "xcvxcv",
                mood = 9955,
                weather = 2682,
                createdAt = 4071,
                updatedAt = 2301,
                imageUrlList = listOf(),
                tagList = listOf()
            )
        )
    )
}

@Preview
@Composable
fun DiaryListScreenEmptyPreview() {
    DiaryListScreen()
}


@Composable
fun calculateItemScale(state: LazyListState, index: Int): Float {
    val configuration = LocalConfiguration.current
    val displayWidth = configuration.screenWidthDp.dp
    val layoutInfo = state.layoutInfo
    val visibleItemsInfo = layoutInfo.visibleItemsInfo

    val itemInfo = visibleItemsInfo.find { it.index == index }
        ?: return 0.8f // Default scale for offscreen items

    val horizontalPadding = with(LocalDensity.current) {
        ((displayWidth / 2) - 130.dp).toPx() / 2
    }
    val centerOfViewport = layoutInfo.viewportEndOffset / 2
    val centerOfItem = itemInfo.offset + itemInfo.size / 2


    val distanceFromCenter = (centerOfItem - centerOfViewport + horizontalPadding).absoluteValue
    val maxDistance = layoutInfo.viewportEndOffset / 2.5f // Adjust sensitivity

    val scale = 1f - (distanceFromCenter / maxDistance).coerceAtMost(1f)
    return max(0.9f, scale)
}