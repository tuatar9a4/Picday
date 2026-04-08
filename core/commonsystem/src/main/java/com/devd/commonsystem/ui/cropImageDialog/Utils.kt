package com.devd.commonsystem.ui.cropImageDialog

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.net.Uri
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max
import kotlin.math.min


private const val ASPECT = 9f / 16f

enum class DragMode {
    None, Move, TopLeft, TopRight, BottomLeft, BottomRight
}

/* 이미지 크기 따른 Rect */
fun IntSize.getRatioRect(topOffset: Offset): Rect {
    val imageRatio = this.width / this.height.toFloat()
    val (width, height) = if (imageRatio > ASPECT) {
        val height = this.height * 0.8f
        height * 9f / 16f to height
    } else {
        val width = this.width * 0.6f
        width to width * 16f / 9f
    }
    val left = (this.width - width) / 2f + topOffset.x
    val top = ((this.height - height) / 2f) + topOffset.y

    return Rect(left, top, left + width, top + height)
}

/* DragMode 감지 */
fun detectMode(
    touch: Offset, rect: Rect
): DragMode {

    val handleSize = 100f

    fun isNear(point: Offset): Boolean {
        return (touch - point).getDistance() < handleSize
    }
    return when {
        isNear(rect.topLeft) -> DragMode.TopLeft
        isNear(rect.topRight) -> DragMode.TopRight
        isNear(rect.bottomLeft) -> DragMode.BottomLeft
        isNear(rect.bottomRight) -> DragMode.BottomRight
        rect.contains(touch) -> DragMode.Move
        else -> DragMode.None
    }
}

/* CropRect 이동 */
fun Rect.translateSafe(
    drag: Offset, containerSize: IntSize, imageOffset: Offset
): Rect {

    val newLeft =
        (left + drag.x).coerceIn(imageOffset.x, imageOffset.x + containerSize.width - width)

    val newTop =
        (top + drag.y).coerceIn(imageOffset.y, imageOffset.y + containerSize.height - height)

    return Rect(
        left = newLeft, top = newTop, right = newLeft + width, bottom = newTop + height
    )
}

/* CropRect Resize */
fun resizeKeepRatioSafe(
    rect: Rect,
    drag: Offset,
    mode: DragMode,
    container: IntSize,
    imageOffset: Offset,
    minSize: Float = 200f
): Rect {

    var left = rect.left
    var top = rect.top
    var right = rect.right
    var bottom = rect.bottom

    when (mode) {

        DragMode.TopLeft -> {
            left += drag.x
            val width = right - left
            val height = width / ASPECT
            top = bottom - height
        }

        DragMode.TopRight -> {
            right += drag.x
            val width = right - left
            val height = width / ASPECT
            top = bottom - height
        }

        DragMode.BottomLeft -> {
            left += drag.x
            val width = right - left
            val height = width / ASPECT
            bottom = top + height
        }

        DragMode.BottomRight -> {
            right += drag.x
            val width = right - left
            val height = width / ASPECT
            bottom = top + height
        }

        else -> return rect
    }
    // 최대 크기(비율) 제한
    val bottomLimit = container.height + imageOffset.y
    val topLimit = 0 + imageOffset.y
    val leftLimit = imageOffset.x
    val rightLimit = container.width + imageOffset.x
    if (bottom > bottomLimit) return rect.copy(bottom = bottomLimit)
    if (top < topLimit) return rect.copy(top = topLimit)
    if (left < leftLimit) return rect.copy(left = leftLimit)
    if (right > rightLimit) return rect.copy(right = rightLimit)

    // 최소 크기 제한
    if (right - left < minSize) return rect

    // 화면 경계 제한
    left = left.coerceIn(leftLimit, rightLimit)
    right = right.coerceIn(leftLimit, rightLimit)
    top = top.coerceIn(topLimit, bottomLimit)
    bottom = bottom.coerceIn(topLimit, bottomLimit)

    return Rect(left, top, right, bottom)
}

/* Scale Zoom */
fun Rect.scaleSafe(
    zoom: Float,
    containerSize: IntSize,
    imageOffset: Offset
): Rect {
    // 1. 현재 중심점 계산
    val centerX = this.left + this.width / 2f
    val centerY = this.top + this.height / 2f

    // 2. 새로운 크기 계산
    val newWidth = this.width * zoom
    val newHeight = this.height * zoom

    // 3. 중심을 유지하며 새로운 Rect 생성
    var newRect = Rect(
        left = centerX - newWidth / 2f,
        top = centerY - newHeight / 2f,
        right = centerX + newWidth / 2f,
        bottom = centerY + newHeight / 2f
    )

    // 4. 경계 제한 (Container 및 ImageOffset 기준)
    // 왼쪽/위 제한
    if (newRect.left < imageOffset.x) {
        newRect = newRect.translate(imageOffset.x - newRect.left, 0f)
    }
    if (newRect.top < imageOffset.y) {
        newRect = newRect.translate(0f, imageOffset.y - newRect.top)
    }

    // 오른쪽/아래 제한 (이미지 크기를 벗어나지 않게 하려면 추가 로직 필요)
    val maxRight = containerSize.width.toFloat() - imageOffset.x
    val maxBottom = containerSize.height.toFloat() - imageOffset.y

    if (newRect.right > maxRight) {
        newRect = newRect.translate(maxRight - newRect.right, 0f)
    }
    if (newRect.bottom > maxBottom) {
        newRect = newRect.translate(0f, maxBottom - newRect.bottom)
    }

    // 최종 크기가 컨테이너보다 커지는 것을 방지
    return if (newRect.width <= (maxRight - imageOffset.x) &&
        newRect.height <= (maxBottom - imageOffset.y)
    ) {
        newRect
    } else {
        this // 너무 커지면 변화 무시
    }
}

/* Uri 를 Bitmap으로 전환 */
fun loadBitmapFromUri(
    context: Context, uri: Uri
): Bitmap {

    val source = ImageDecoder.createSource(
        context.contentResolver, uri
    )

    return ImageDecoder.decodeBitmap(source) { decoder, info, _ ->

        // 필요하면 여기서 리사이즈 가능
        val maxSize = 2048

        val width = info.size.width
        val height = info.size.height

        if (width > maxSize || height > maxSize) {
            val scale = maxSize / max(width, height).toFloat()
            decoder.setTargetSize(
                (width * scale).toInt(), (height * scale).toInt()
            )
        }
    }
}

/* Crop */
fun Context.cropAndSave(
    bitmap: Bitmap,
    cropRect: Rect,
    imageOffset: Offset,
    containerSize: IntSize
): File {

    val scale = min(
        containerSize.width.toFloat() / bitmap.width, containerSize.height.toFloat() / bitmap.height
    )

    val displayWidth = bitmap.width * scale
    val displayHeight = bitmap.height * scale

    val offsetX = (containerSize.width - displayWidth) / 2f
    val offsetY = (containerSize.height - displayHeight) / 2f
    val x = ((cropRect.left - imageOffset.x - offsetX) / scale).toInt()
    val y = ((cropRect.top - imageOffset.y - offsetY) / scale).toInt()
    val w = (cropRect.width / scale).toInt()
    val h = (cropRect.height / scale).toInt()

    val cropped = Bitmap.createBitmap(bitmap, x, y, w, h)

    val file = File(
        cacheDir, "cropped_${System.currentTimeMillis()}.jpg"
    )

    FileOutputStream(file).use {
        cropped.compress(Bitmap.CompressFormat.JPEG, 100, it)
    }

    return file
}


/**TEmp***/

fun createRainbowBitmap(
    width: Int,
    height: Int
): Bitmap {

    val bitmap = Bitmap.createBitmap(
        width,
        height,
        Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(bitmap)

    val paint = Paint()

    val shader = LinearGradient(
        0f, 0f,
        width.toFloat(), height.toFloat(),
        intArrayOf(
            Color.RED,
            Color.YELLOW,
            Color.GREEN,
            Color.CYAN,
            Color.BLUE,
            Color.MAGENTA
        ),
        null,
        Shader.TileMode.CLAMP
    )

    paint.shader = shader

    canvas.drawRect(
        0f, 0f,
        width.toFloat(),
        height.toFloat(),
        paint
    )

    return bitmap
}