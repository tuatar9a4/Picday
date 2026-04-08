package com.devd.commonsystem.ui.cropImageDialog

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
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
                detectTransformGestures { centroid, pan, zoom, rotation ->
                    val rect = cropRect.value

                    // 1. 모드 결정 (터치 시작 시점이 따로 없으므로 중심점(centroid) 기준으로 판단)
                    // 핀치 줌 중이 아닐 때(zoom == 1f) 드래그 모드를 체크하거나,
                    // 항상 드래그 모드를 갱신하도록 구성할 수 있습니다.
                    if (mode == DragMode.None || zoom == 1f) {
                        mode = detectMode(centroid, rect)
                    }

                    // 2. 변화 적용
                    cropRect.value = when {
                        // 핀치 줌이 발생한 경우 (zoom이 1.0이 아님)
                        zoom != 1f -> {
                            val newSize = rect.size * zoom
                            // 중심을 기준으로 확장/축소 하거나 필요에 따라 로직 수정
                            // 여기서는 단순 크기 조절 예시를 위해 resize 함수 재사용 권장
                            rect.scaleSafe(zoom, containerSize, imageOffset)
                        }

                        // 단순 이동 (드래그)
                        mode == DragMode.Move -> {
                            rect.translateSafe(pan, containerSize, imageOffset)
                        }

                        // 모서리 핸들 드래그
                        mode != DragMode.None -> {
                            resizeKeepRatioSafe(rect, pan, mode, containerSize, imageOffset)
                        }

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