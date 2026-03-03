package com.devd.diary.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.BlackOpacity40Color
import com.devd.commonsystem.theme.PrimaryColor
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.utils.AnimateAsyncImage
import com.devd.commonsystem.utils.LocalAnimatedVisibilityScope
import com.devd.commonsystem.utils.LocalSharedTransitionScope
import com.devd.commonsystem.utils.rememberImageUrl
import com.devd.diary.data.CanScrollDirection
import com.devd.diary.data.isShowLeftScroll
import com.devd.diary.data.isShowRightScroll
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

@Composable
fun DiaryImageScreen(
    pagerState : PagerState,
    imageList: List<String?>
) {
    val scope = rememberCoroutineScope()
    val sharedScope = LocalSharedTransitionScope.current
    val animatedScope = LocalAnimatedVisibilityScope.current

    val isCanScroll = remember { mutableStateOf(CanScrollDirection.CAN_SCROLL_ANYWHERE) }

    LaunchedEffect(pagerState.currentPage) {
        when {
            (pagerState.pageCount == 1) -> isCanScroll.value =
                CanScrollDirection.CAN_NOT_SCROLL_ANYWHERE

            (pagerState.currentPage == 0) -> isCanScroll.value =
                CanScrollDirection.CAN_NOT_SCROLL_RIGHT

            (pagerState.currentPage == (pagerState.pageCount - 1)) ->
                isCanScroll.value = CanScrollDirection.CAN_NOT_SCROLL_LEFT

            else -> isCanScroll.value = CanScrollDirection.CAN_SCROLL_ANYWHERE
        }
    }

    fun moveToPage(tagetPage: Int) {
        scope.launch { pagerState.animateScrollToPage(tagetPage) }
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalPager(
            state = pagerState,
            reverseLayout = true
        ) { page ->
            val pageInfo = imageList[page]
            pageInfo?.let {
                sharedScope.AnimateAsyncImage(
                    modifier =Modifier
                        .align(Alignment.TopCenter)
                        .aspectRatio(9 / 16f)
                        .background(color = PrimaryColor),
                    model = pageInfo.rememberImageUrl(),
                    key =  "image-$page",
                    animatedVisibilityScope = animatedScope
                )
            } ?: run {
                Image(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .aspectRatio(9 / 16f)
                        .background(color = PrimaryColor),
                    painter = painterResource(R.drawable.icon_photo),
                    contentDescription = null
                )

            }
        }
        if (isCanScroll.value.isShowLeftScroll()) {
            Image(
                modifier = Modifier
                    .padding(start = 15.dp, bottom = 40.dp)
                    .size(36.dp)
                    .clickable(onClick = {
                        moveToPage(
                            min(pagerState.currentPage + 1, pagerState.pageCount - 1)
                        )
                    })
                    .align(Alignment.CenterStart)
                    .background(BlackOpacity40Color, shape = CircleShape),
                painter = painterResource(R.drawable.icon_andgle_left),
                contentDescription = null,
                colorFilter = ColorFilter.tint(WhiteColor)
            )
        }
        if (isCanScroll.value.isShowRightScroll()) {
            Image(
                modifier = Modifier
                    .padding(end = 15.dp, bottom = 40.dp)
                    .size(36.dp)
                    .clickable(onClick = {
                        moveToPage(
                            max(pagerState.currentPage - 1, 0)
                        )
                    })
                    .align(Alignment.CenterEnd)
                    .background(BlackOpacity40Color, shape = CircleShape),
                painter = painterResource(R.drawable.icon_angle_right),
                contentDescription = null,
                colorFilter = ColorFilter.tint(WhiteColor)
            )
        }
    }

}