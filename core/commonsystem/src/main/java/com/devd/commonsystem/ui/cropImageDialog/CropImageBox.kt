package com.devd.commonsystem.ui.cropImageDialog

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.RedColor

@Composable
fun CropImageBox(
    modifier: Modifier = Modifier,
    containerSize: MutableState<IntSize> = mutableStateOf(IntSize.Zero),
    imageOffset: MutableState<Offset> = mutableStateOf(Offset.Zero),
    cropRect: MutableState<Rect> = mutableStateOf(Rect.Zero),
    imgBitmap: Bitmap
) {

    var isVerticalImage by remember { mutableStateOf(false) }

    LaunchedEffect(imageOffset) {
        cropRect.value = containerSize.value.getRatioRect(imageOffset.value)
    }

    Box(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .background(color = BlackColor)
                .onSizeChanged {
                    isVerticalImage =
                        (imgBitmap.width.toFloat() / imgBitmap.height) < (it.width.toFloat() / it.height)
                }
        )
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.Center)
                .background(color = RedColor)
                .then(
                    if (isVerticalImage) Modifier.fillMaxHeight()
                    else Modifier.fillMaxWidth()
                )
                .onGloballyPositioned { coordinates ->
                    imageOffset.value = coordinates.positionInParent()
                }
                .onSizeChanged {
                    containerSize.value = it
                },
            bitmap = imgBitmap.asImageBitmap(),
            contentScale = if (isVerticalImage) ContentScale.FillHeight else ContentScale.FillWidth,
            contentDescription = null
        )
        CropOverlay(
            cropRect = cropRect,
            containerSize = containerSize.value,
            imageOffset = imageOffset.value
        )
    }
}

@Preview
@Composable
fun CropImageBoxPreview() {
    CropImageBox(
        imgBitmap = createRainbowBitmap(1080, 1900)
    )
}