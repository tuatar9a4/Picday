package com.devd.bookcase.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devd.commonsystem.theme.AccentDimColor
import com.devd.commonsystem.theme.BlackOpacity40Color
import com.devd.commonsystem.theme.CalmNaturePoint
import com.devd.commonsystem.theme.MidnightMain
import com.devd.commonsystem.theme.RedColor
import com.devd.commonsystem.theme.SecondaryColor
import com.devd.commonsystem.theme.SoftPastelPoint
import com.devd.commonsystem.theme.YellowColor
import kotlinx.coroutines.delay


data class ColorItem(
    val id: String,
    val color: Color,
    val subColor: Color,
)

@Preview
@Composable
fun TranscationTestPreview() {
    val colorList = listOf<ColorItem>(
        ColorItem("1", YellowColor, AccentDimColor),
        ColorItem("2", CalmNaturePoint, YellowColor),
        ColorItem("3", SoftPastelPoint, MidnightMain),
        ColorItem("4", AccentDimColor, SoftPastelPoint),
        ColorItem("5", RedColor, SecondaryColor)
    )


    var selectItem by remember { mutableStateOf<ColorItem?>(null) }
    val itemList by remember { mutableStateOf(colorList) }
    SharedTransitionLayout(modifier = Modifier.fillMaxWidth()) {
        Box() {
            Column(
                modifier = Modifier.fillMaxSize()
                    .animateContentSize( // 내부 요소 변화 시 부드럽게 크기 조절
                        animationSpec = spring(stiffness = Spring.StiffnessLow)
                    )
            ) {
                Text(
                    modifier = Modifier
                        .height(30.dp)
                        .fillMaxWidth(), text = "asdasdsa"
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(40.dp),
                    contentPadding = PaddingValues(horizontal = 75.dp)
                ) {
                    items(itemList) {
                        AnimatedVisibility(
                            visible = it != selectItem,
                            modifier = Modifier.animateItem()
                        ) {
                            Column() {
                                Text(text = "!!!!!! ${it.id}")
                                Box(
                                    modifier = Modifier
                                        .sharedBounds(
                                            sharedContentState = rememberSharedContentState(key = "${it.id}-bounds"),
                                            // Using the scope provided by AnimatedVisibility
                                            animatedVisibilityScope = this@AnimatedVisibility,
                                        )
                                        .background(it.color)
                                        .width(200.dp)
                                        .height(300.dp)
                                        .clickable(onClick = { selectItem = it }),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        modifier = Modifier.align(Alignment.Center),
                                        text = it.id,
                                        fontSize = 30.sp
                                    )
                                }
                            }
                        }
                    }
                }
                Text(
                    modifier = Modifier
                        .height(30.dp)
                        .fillMaxWidth(), text = "Button"
                )
            }
        }
        OpendBook22(
            colorItem = selectItem
        ) {
            selectItem = null
        }


    }
}

@Composable
fun ColorItem(
    modifier: Modifier
) {
    Box(
        modifier = modifier
    ) {

    }
}


@Composable
fun SharedTransitionScope.OpendBook22(
    colorItem: ColorItem?,
    onItemClick: () -> Unit
) {
    val bookWidth = LocalWindowInfo.current.containerDpSize.width - 150.dp
    val bookHeight = bookWidth * 16 / 9f
    val isOpened = remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isOpened.value) -180f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "FlipAnimation"
    )

    LaunchedEffect(colorItem) {
        if (colorItem != null) {
            delay(100)
            isOpened.value = true
        }
    }

    BackHandler() {
        isOpened.value = false
    }

    LaunchedEffect(rotation) {
        if (!isOpened.value && rotation > -60f && rotation != 0f) {
//                selectBook.value = null
//            bookClickAction(BookcaseInterface.OnColesDiaryBook)
            onItemClick()
        }
    }

    AnimatedContent(
        targetState = colorItem,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = "SnackEditDetails"
    ) { tragetColor ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (tragetColor != null) {
                Box(
                    modifier = Modifier
                        .clickable(onClick = { isOpened.value = false })
                        .fillMaxSize()
                        .background(color = BlackOpacity40Color),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .sharedBounds(
                                sharedContentState = rememberSharedContentState(key = "${tragetColor.id}-bounds"),
                                // Using the scope provided by AnimatedVisibility
                                animatedVisibilityScope = this@AnimatedContent,
                                //                                clipInOverlayDuringTransition = OverlayClip(shapeForSharedElement)
                            )
                            .background(tragetColor.color)
                            .width(bookWidth)
                            .height(bookHeight),
                        contentAlignment = Alignment.Center
                    ) {
                        //inner
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(tragetColor.subColor)
                        ) {

                        }
                        //COver
                        Box(
                            modifier = Modifier
                                .graphicsLayer {
                                    rotationY = rotation
                                    transformOrigin = TransformOrigin(0f, 0.5f)
                                    cameraDistance = 12f * density
                                }
                                .fillMaxSize()
                                .background(tragetColor.color)
                        ) {

                        }
                    }
                }
            }
        }

    }
}