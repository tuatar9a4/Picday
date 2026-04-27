package com.devd.diary.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.utils.AnimateAsyncImage
import com.devd.commonsystem.utils.LocalAnimatedVisibilityScope
import com.devd.commonsystem.utils.LocalSharedTransitionScope
import com.devd.commonsystem.utils.rememberImageUrl

@Composable
fun DiaryImageScreen(
    modifier: Modifier,
    pagerState: PagerState,
    imageList: List<String?>
) {
    val sharedScope = LocalSharedTransitionScope.current
    val animatedScope = LocalAnimatedVisibilityScope.current

    Box(
        modifier = modifier.then(Modifier.fillMaxWidth())
    ) {
        HorizontalPager(
            state = pagerState,
            reverseLayout = true
        ) { page ->
            val pageInfo = imageList[page]
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(color = BlackColor)
            ){
                pageInfo?.let {
                    sharedScope.AnimateAsyncImage(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .aspectRatio(9 / 16f),
                        model = pageInfo.rememberImageUrl(),
                        key = "image-$page",
                        animatedVisibilityScope = animatedScope
                    )
                } ?: run {
                    Image(
                        modifier = Modifier
                            .align(Alignment.Center),
                        painter = painterResource(R.drawable.icon_camera),
                        contentDescription = null
                    )

                }
            }
        }
    }

}