package com.devd.bookcase.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.devd.commonsystem.theme.Black7COp66Color
import com.devd.commonsystem.theme.BlackF2Color
import com.devd.commonsystem.theme.TransParents
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.utils.dropShadow

@Preview
@Composable
fun BookCoveoprPreview() {
    val rotation = -0f
    Box(
        modifier = Modifier
            .graphicsLayer(clip = false)
            .width(500.dp)
            .height(600.dp)
            .background(WhiteColor),
        contentAlignment = Alignment.Center,
    ) {
        BookCover(
            modifier = Modifier.graphicsLayer {
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
        modifier = modifier.then(
            Modifier.graphicsLayer{clip=false}
        )
    ) {
        if (isOpen && coverImage == null) { // 펼쳐지고 난 뒤의 하얀배경
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp))
                    .background(WhiteColor)
                    .width(leftWidth + rightWidth)
                    .height(totalHeight)
            )
        } else {
            Row(
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .width(bookSize.width.dp)
                        .height(totalHeight)
                        .dropShadow(
                            shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp),
                            offsetX = 1.dp,
                            offsetY = 2.dp,
                            spread = 2.dp,
                            blur = 4.dp,
                        )
                ) {
                    AsyncImage(
                        model = coverImage,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .wrapContentSize(
                                align = Alignment.CenterStart,
                                unbounded = true
                            )
                            .width(leftWidth + rightWidth)
                            .height(totalHeight)
                            .clip(RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp))
                    )
//                    Image(
//                        painterResource(R.drawable.text_book_case_image),
//                        contentDescription = null,
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier
//                            .wrapContentSize(align = Alignment.CenterEnd, unbounded = true)
//                            .width(leftWidth + rightWidth) // 전체 이미지가 들어갈 수 있는 너비 확보
//                            .height(totalHeight)
//                            .clip(RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp))
//                    )
                    Box(
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .width(5.dp)
                            .fillMaxHeight()
                            .background(
                                brush = Brush.horizontalGradient(
                                    0f to Black7COp66Color,
                                    0.65f to BlackF2Color.copy(alpha = 0.14f),
                                    1f to TransParents
                                )
                            ),
                    )
                }
            }
        }
    }
}