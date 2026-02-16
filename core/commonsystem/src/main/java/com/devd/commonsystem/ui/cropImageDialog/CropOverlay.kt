package com.devd.commonsystem.ui.cropImageDialog

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.WhiteColor


@Composable
fun CropOverlay(
    cropRect: MutableState<Rect>,
    containerSize: IntSize,
    imageOffset : Offset
) {
    var mode by remember { mutableStateOf(DragMode.None) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        mode = detectMode(offset, cropRect.value)
                    },
                    onDragEnd = { mode = DragMode.None }
                ) { change, dragAmount ->

                    change.consume()

                    val rect = cropRect.value

                    cropRect.value = when (mode) {
                        DragMode.Move -> rect.translateSafe(dragAmount, containerSize,imageOffset)
                        DragMode.TopLeft,
                        DragMode.TopRight,
                        DragMode.BottomLeft,
                        DragMode.BottomRight ->
                            resizeKeepRatioSafe(rect, dragAmount, mode, containerSize,imageOffset)

                        else -> rect
                    }
                }
            }
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            }
    ) {
        drawRect(BlackColor.copy(alpha = 0.5f)) // background
        drawRect(
            Transparent.copy(alpha = 0f),
            topLeft = cropRect.value.topLeft,
            size = cropRect.value.size,
            blendMode = BlendMode.Clear
        ) // CropZone
        listOf(
            cropRect.value.topLeft,
            cropRect.value.topRight,
            cropRect.value.bottomLeft,
            cropRect.value.bottomRight
        ).forEach {
            drawCircle(
                color = WhiteColor,
                radius = 10.dp.toPx(),
                center = it
            )
        } // Handle
    }
}