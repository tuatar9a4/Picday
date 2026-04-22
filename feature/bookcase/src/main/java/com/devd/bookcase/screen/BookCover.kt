package com.devd.bookcase.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.devd.commonsystem.theme.Black04Opacity30Color
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.TransParents
import com.devd.commonsystem.theme.WhiteColor


@Preview
@Composable
fun BookCoverPreview() {
    BookCover(
        bookSize = IntSize(200, 300),
        coverImage = "",
        isOpen = false
    )
}
@Preview
@Composable
fun BookCoveoprPreview() {
    val rotation = -0f
    Box(
        modifier = Modifier
            .graphicsLayer(clip = false)
            .width(500.dp)
            .height(600.dp)
            .background(BlackColor),
        contentAlignment = Alignment.Center,
    ) {
        BookCover(
            modifier = Modifier.background(WhiteColor).graphicsLayer {
                rotationY = rotation
                transformOrigin = TransformOrigin(0f, 0.5f)
                cameraDistance = 12f * density
            },
            bookSize = IntSize(200, 300),
            coverImage = null,
            isOpen = rotation < -90f
        )
    }

}

@Composable
fun BookCover(
    modifier: Modifier = Modifier,
    bookSize: IntSize,
    coverImage: String?,
    isOpen: Boolean
) {

    val totalHeight = bookSize.height.dp
    val leftWidth = (bookSize.width * 0.125f).dp  // 왼쪽 면 너비
    val rightWidth = (bookSize.width * 0.875f).dp // 오른쪽 면 너비
    Box(
        modifier = modifier
    ) {
        if (isOpen && coverImage==null) {
            Box(
                modifier = Modifier
                    .background(WhiteColor)
                    .width(leftWidth + rightWidth)
                    .height(totalHeight)
            )
        } else {
            Row(
                modifier = Modifier
                    .width(leftWidth + rightWidth)
                    .align(Alignment.Center)
                    .graphicsLayer {
                        cameraDistance = 15f * density // 원근감
                        translationX = -2f
                    }
            ) {
                //책 왼쪽
                Box(
                    modifier = Modifier
                        .width(leftWidth)
                        .height(totalHeight)
                        .graphicsLayer {
                            rotationY = -10f // 많이 회전
                            transformOrigin = TransformOrigin(1f, 0.5f) // 오른쪽 기준
                        }
                        .clipToBounds()
                ) {
                    // 여기에 같은 이미지를 넣습니다.
                    coverImage?.let {
                        AsyncImage(
                            model = coverImage,
                            contentDescription = "Left Cover Part",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .wrapContentSize(
                                    align = Alignment.CenterStart,
                                    unbounded = true
                                )
                                .width(leftWidth + rightWidth)
                                .height(totalHeight)
                        )
//                        Image(
//                            painterResource(R.drawable.text_book_case_image),
//                            contentDescription = null,
//                            contentScale = ContentScale.Crop,
//                            modifier = Modifier
//                                .wrapContentSize(
//                                    align = Alignment.CenterStart,
//                                    unbounded = true
//                                )
//                                .width(leftWidth + rightWidth)
//                                .height(totalHeight)
//                        )
                        Row(
                            Modifier.fillMaxSize()
                        ) {
                            Box(
                                Modifier
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            0.0f to Black04Opacity30Color,
                                            0.8f to Black04Opacity30Color,
                                            1.0f to Transparent
                                        )
                                    )
                                    .weight(1f)
                                    .fillMaxHeight()
                            )
                            Box(
                                modifier
                                    .background(TransParents)
                                    .width(1.dp)
                                    .fillMaxHeight()
                            )
                            Box(
                                modifier
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            0.0f to Transparent,
                                            0.3f to Black04Opacity30Color,
                                            0.7f to Transparent
                                        )
                                    )
                                    .width(4.dp)
                                    .fillMaxHeight()
                            )
                        }
                    } ?: run {
                        Box(
                            modifier = Modifier
                                .background(WhiteColor)
                                .wrapContentSize(
                                    align = Alignment.CenterStart,
                                    unbounded = true
                                )
                                .width(leftWidth + rightWidth)
                                .height(totalHeight)
                        )
                    }
                }

//            오른쪽 커버
                Box(
                    modifier = Modifier
                        .width(rightWidth)
                        .height(totalHeight)
                        .graphicsLayer {
                            rotationY = 5f // 살짝 회전
                            transformOrigin = TransformOrigin(0f, 0.5f) // 왼쪽 기준
                        }
                        .clipToBounds()
                ) {
                    coverImage?.let {
                        AsyncImage(
                            model = coverImage,
                            contentDescription = "Right Cover Part",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .wrapContentSize(align = Alignment.CenterEnd, unbounded = true)
                                .width(leftWidth + rightWidth) // 전체 이미지가 들어갈 수 있는 너비 확보
                                .height(totalHeight)
                        )
//                        Image(
//                            painterResource(R.drawable.text_book_case_image),
//                            contentDescription = null,
//                            contentScale = ContentScale.Crop,
//                            modifier = Modifier
//                                .wrapContentSize(align = Alignment.CenterEnd, unbounded = true)
//                                .width(leftWidth + rightWidth) // 전체 이미지가 들어갈 수 있는 너비 확보
//                                .height(totalHeight)
//                        )
                    } ?: run {
                        Box(
                            modifier = Modifier
                                .background(WhiteColor)
                                .wrapContentSize(
                                    align = Alignment.CenterStart,
                                    unbounded = true
                                )
                                .width(leftWidth + rightWidth)
                                .height(totalHeight)
                        )
                    }
                }
            }
        }
    }
}