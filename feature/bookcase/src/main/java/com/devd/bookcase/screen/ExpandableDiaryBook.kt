package com.devd.bookcase.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.devd.bookcase.BookcaseInterface
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.AccentColor
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.BlackOpacity90Color
import com.devd.commonsystem.theme.GreyOpacity40Color
import com.devd.commonsystem.theme.OneDayTypography
import com.devd.commonsystem.theme.YellowColor
import com.devd.commonsystem.utils.noRippleClickable
import com.devd.commonsystem.utils.rememberImageUrl
import com.devd.model.local.DiaryBookInfo
import com.devd.model.local.DiaryPhaseType
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Preview
@Composable
fun ExpandableDiaryBookPreview() {
    ExpandableDiaryBook(
        pagerState = rememberPagerState(pageCount = { 4 }),
        isOpened = mutableStateOf(false),
        bookList = listOf(
            DiaryBookInfo(
                bookId = 9764,
                bookImage = "dicit",
                title = "errem",
                description = "omnesque",
                bookPhaseType = DiaryPhaseType.MOON,
                createDate = 7837,
                monthWritePercent = 2.3f,
                isMajor = true
            ), DiaryBookInfo(
                bookId = 3851,
                bookImage = "verterem",
                title = "sagittis",
                description = "explicari",
                bookPhaseType = DiaryPhaseType.MOON,
                createDate = 2498,
                monthWritePercent = 8.9f
            ),
            DiaryBookInfo(
                bookId = 8879,
                bookImage = "tractatos",
                title = "pericula",
                description = "torquent",
                bookPhaseType = DiaryPhaseType.MOON,
                createDate = 5329,
                monthWritePercent = 12.13f
            ),
            DiaryBookInfo(
                bookId = 9260,
                bookImage = "urna",
                title = "justo",
                description = "maiorum",
                bookPhaseType = DiaryPhaseType.MOON,
                createDate = 6718,
                monthWritePercent = 16.17f

            )
        ),
        bookClickAction = {}
    )
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ExpandableDiaryBook(
    pagerState: PagerState,
    isOpened: MutableState<Boolean> = mutableStateOf(false),
    bookList: List<DiaryBookInfo>,
    bookClickAction: (BookcaseInterface) -> Unit,
) {
    // 선택된 책의 ID (null이면 리스트 화면, 값이 있으면 상세 화면)
    var selectedBookId by remember { mutableStateOf<DiaryBookInfo?>(null) }

    val bookWidth = LocalWindowInfo.current.containerDpSize.width - 150.dp
    val bookHeight = bookWidth * 16 / 9f

    //선택된 일기장의 확장 Scale
    val scale by animateFloatAsState(
        targetValue = if (isOpened.value) 1.2f else 1f,
        animationSpec = tween(1000)
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.icon_stroke_crown),
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    if (bookList.getOrNull(pagerState.currentPage)?.isMajor == true) YellowColor else GreyOpacity40Color
                )
            )
            Spacer(Modifier.width(5.dp))
            Text(
                text = bookList.getOrNull(pagerState.currentPage)?.title ?: "",
                style = OneDayTypography.titleLarge
            )
        }
        Spacer(Modifier.height(10.dp))
        SharedTransitionLayout(modifier = Modifier.fillMaxWidth()) {
            AnimatedContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bookHeight * 1.2f),
                targetState = selectedBookId?.bookId,
                label = "BookTransition"
            ) { targetId ->
                if (targetId == null) {
                    //BookCover List
                    HorizontalPager(
                        state = pagerState,
                        contentPadding = PaddingValues(horizontal = 75.dp),
                        modifier = Modifier.fillMaxWidth(),
                        pageSpacing = 50.dp,
                        verticalAlignment = Alignment.CenterVertically
                    ) { page ->
                        val bookInfo = bookList[page]
                        Box(
                            modifier = Modifier.sharedElement(
                                sharedContentState = rememberSharedContentState(key = "book_${bookInfo.bookId}"),
                                animatedVisibilityScope = this@AnimatedContent
                            )
                        ) {
                            BookCover(
                                modifier = Modifier
                                    .width(bookWidth)
                                    .aspectRatio(9 / 16f)
                                    .background(AccentColor, RoundedCornerShape(4.dp))
                                    .clickable {
                                        bookClickAction(BookcaseInterface.OnOpenDiaryBook(bookInfo.bookId))
                                        selectedBookId = bookInfo
                                    },
                                bookInfo = bookInfo
                            )
                        }
                    }
                } else {
                    //Select BookDiary
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {

                        LaunchedEffect(Unit) {
                            delay(100)
                            isOpened.value = true
                        }

                        // 회전 애니메이션
                        val rotation by animateFloatAsState(
                            targetValue = if (isOpened.value) -180f else 0f,
                            animationSpec = tween(800, easing = FastOutSlowInEasing),
                            label = "FlipAnimation"
                        )

                        LaunchedEffect(rotation) {
                            if (!isOpened.value && rotation > -60f && rotation != 0f) selectedBookId =
                                null
                        }

                        Box(
                            modifier = Modifier
                                .size(bookWidth)
                                .aspectRatio(9 / 16f)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .sharedElement(
                                    sharedContentState = rememberSharedContentState(key = "book_$targetId"),
                                    animatedVisibilityScope = this@AnimatedContent
                                )
                                .noRippleClickable {
                                    isOpened.value = false
                                }
                        ) {
                            //본 페이지
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(Color.White)
                            )
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
                            //왼쪽 페이지
                            BookCover(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer {
                                        rotationY = rotation
                                        transformOrigin = TransformOrigin(0f, 0.5f)
                                        cameraDistance = 12f * density
                                    }
                                    .background(if (rotation > -90f) AccentColor else Color.White),
                                bookInfo = if (rotation < -90f) null else selectedBookId
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun BookCover(
    modifier: Modifier,
    bookInfo: DiaryBookInfo?
) {
    Column(
        modifier = modifier.then(
            Modifier
                .padding(horizontal = 20.dp, vertical = 50.dp)
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        bookInfo?.let {
            val createDate = remember {
                Instant.ofEpochMilli(bookInfo.createDate)
                    .atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
            }
            AsyncImage(
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .fillMaxWidth()
                    .aspectRatio(1 / 1f)
                    .clip(RoundedCornerShape(5.dp))
                    .background(BlackColor),
                model = bookInfo.bookImage?.rememberImageUrl(),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
            Spacer(Modifier.height(20.dp))
            Text(text = "생성일", style = OneDayTypography.labelLarge)
            Spacer(Modifier.height(5.dp))
            Text(text = createDate, style = OneDayTypography.bodyMedium)
        }
    }
}