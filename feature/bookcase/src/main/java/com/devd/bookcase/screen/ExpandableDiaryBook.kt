package com.devd.bookcase.screen

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import com.devd.commonsystem.utils.rememberImageUrl
import com.devd.model.local.DiaryBookInfo
import com.devd.model.local.DiaryInfo
import com.devd.model.local.DiaryPhaseType


@Preview
@Composable
fun ExpandableDiaryBookPreview() {
    ExpandableDiaryBook(
        pagerState = rememberPagerState(pageCount = { 4 }),
        isOpened = remember { mutableStateOf(false) },
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
        diaryList = emptyList(),
        onMoreClick = {},
        selectedBookId = remember { mutableStateOf<DiaryBookInfo?>(null) },
        bookClickAction = {}
    )
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ExpandableDiaryBook(
    sharedModifier: Modifier = Modifier,
    pagerState: PagerState,
    selectedBookId: MutableState<DiaryBookInfo?>,
    isOpened: MutableState<Boolean> = mutableStateOf(false),
    bookList: List<DiaryBookInfo>,
    diaryList: List<DiaryInfo>,
    onMoreClick: (DiaryBookInfo) -> Unit,
    bookClickAction: (BookcaseInterface) -> Unit,
) {
    // 선택된 책의 ID (null이면 리스트 화면, 값이 있으면 상세 화면)
//    var selectedBookId by remember { mutableStateOf<DiaryBookInfo?>(null) }

    val bookWidth = LocalWindowInfo.current.containerDpSize.width - 150.dp
    val bookHeight = bookWidth * 16 / 9f

    // diaryListState
    val state = rememberPagerState(0) { diaryList.size }

    //선택된 일기장의 확장 Scale
    val scale by animateFloatAsState(
        targetValue = if (isOpened.value) 1.2f else 1f,
        animationSpec = tween(1000)
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(10.dp))
//        if (targetId == null) {
        //BookCover List
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(start = 100.dp, end = 50.dp),
            modifier = Modifier.fillMaxWidth(),
            pageSpacing = 20.dp,
            verticalAlignment = Alignment.CenterVertically
        ) { page ->
            val bookInfo = bookList[page]
            Box(
                modifier = sharedModifier
            ) {
                bookInfo.bookImage?.rememberImageUrl()?.let {
                    BookCoverItem(
                        coverImage = it,
                        bookSize = IntSize(200, 300),
                        isOpen = false,
                        bookInfo = bookInfo,
//                        bookName = bookInfo.title,
//                        bookCreateAt = bookInfo.createDate.getDateStr("yyyy.MM.dd"),
//                        bookDescription = bookInfo.description ?: "",
//                        isMajorBook = bookInfo.isMajor,
                        onMoreClick = {
                            //bookClickAction(BookcaseInterface.OnUpdateDiaryBook(bookInfo))
                            onMoreClick(bookInfo)
                        },
                        onChangeMajor = {
                            val bookId = bookList[pagerState.currentPage].bookId
                            bookClickAction.invoke(
                                BookcaseInterface.OnUpdateMajorBook(
                                    bookId
                                )
                            )
                        }
                    )
                }
            }
        }
//        } else {
//            //Select BookDiary
//            Box(
//                modifier = Modifier.fillMaxWidth(),
//                contentAlignment = Alignment.Center
//            ) {
//
//                LaunchedEffect(Unit) {
//                    delay(100)
//                    isOpened.value = true
//                }
//                // 회전 애니메이션
//                val rotation by animateFloatAsState(
//                    targetValue = if (isOpened.value) -180f else 0f,
//                    animationSpec = tween(800, easing = FastOutSlowInEasing),
//                    label = "FlipAnimation"
//                )
//
//                LaunchedEffect(rotation) {
//                    if (!isOpened.value && rotation > -60f && rotation != 0f) {
//                        selectedBookId = null
//                        bookClickAction(BookcaseInterface.OnColesDiaryBook)
//                    }
//                }
//
//                Box(
//                    modifier = Modifier
//                        .size(bookWidth)
//                        .aspectRatio(9 / 16f)
//                        .graphicsLayer {
//                            scaleX = scale
//                            scaleY = scale
//                        }
//                        .sharedElement(
//                            sharedContentState = rememberSharedContentState(key = "book_$targetId"),
//                            animatedVisibilityScope = this@AnimatedContent
//                        )
//                        .noRippleClickable {
//                            isOpened.value = false
//                        }
//                ) {
//                    //본 페이지
//                    HorizontalPager(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .background(Color.White)
//                            .padding(20.dp),
//                        state = state
//                    ) { page ->
//                        DiaryCardScreen(diaryInfo = diaryList[page])
//                    }
//                    Box(
//                        modifier = Modifier
//                            .align(Alignment.CenterStart)
//                            .fillMaxHeight()
//                            .padding(vertical = 1.dp)
//                            .width(8.dp)
//                            .background(
//                                brush = Brush.horizontalGradient(
//                                    colors = listOf(
//                                        Color.Transparent,       // 왼쪽 끝: 투명
//                                        BlackOpacity90Color,      // 중앙: 진하게
//                                        Color.Transparent        // 오른쪽 끝: 투명
//                                    )
//                                )
//                            )
//                    )
//                    //왼쪽 페이지
//                    BookCoverItem(
//                        modifier = Modifier
//                            .graphicsLayer {
//                                rotationY = rotation
//                                transformOrigin = TransformOrigin(0f, 0.5f)
//                                cameraDistance = 12f * density
//                            }
//                            .fillMaxSize()
//                            .background(WhiteColor),
//                        bookSize = IntSize(200, 300),
//                        isMajorBook = selectedBookId?.isMajor == true,
//                        isOpen = true,
//                        coverImage = if (rotation < -90f) null else selectedBookId?.bookImage?.rememberImageUrl()
//                    )
//                }
//            }
//        }
    }
}