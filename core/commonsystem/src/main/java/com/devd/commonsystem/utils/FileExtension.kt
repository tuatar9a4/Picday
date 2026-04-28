package com.devd.commonsystem.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.graphics.scale
import java.io.File
import java.io.FileOutputStream

fun Context.getFileName(uri: Uri): String? {
    if (uri.scheme == "content") {
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && index != -1) {
                return cursor.getString(index)
            }
        }
    }
    return uri.lastPathSegment
}

fun Context.optimizeUriToFile(
    uri: Uri,
    reqWidth: Int = 1280,
    reqHeight: Int = 1280
): File? {
    // 1. 샘플링을 적용하여 비트맵 로드 (메모리 효율적 로딩)
    val sampledBitmap = decodeSampledBitmapFromUri(uri, reqWidth, reqHeight) ?: return null

    // 2. 정확한 해상도로 리사이징 (원하는 크기에 가깝게 2차 조정)
    val resizedBitmap = resizeBitmap(sampledBitmap, reqWidth, reqHeight)

    // 3. 파일 생성 및 압축 저장
    val fileName = "optimized_image_${System.currentTimeMillis()}.webp"
    val file = File(cacheDir, fileName)

    return try {
        FileOutputStream(file).use { out ->
            // WebP 포맷 사용 (용량 대비 화질 우수)
            // Android 10(Q) 이상 대응을 위해 분기 처리 가능하지만,
            // 범용적으로 WEBP 또는 WEBP_LOSSY 사용
            val format = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                Bitmap.CompressFormat.WEBP_LOSSY
            } else {
                @Suppress("DEPRECATION")
                Bitmap.CompressFormat.WEBP
            }

            // 퀄리티는 80~85 정도가 화질 저하 없이 용량을 크게 줄임
            resizedBitmap.compress(format, 85, out)
        }
        // 메모리 해제
        if (sampledBitmap != resizedBitmap) sampledBitmap.recycle()
        resizedBitmap.recycle()

        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun Context.decodeSampledBitmapFromUri(uri: Uri, reqWidth: Int, reqHeight: Int): Bitmap? {
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, options)
    }

    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
    options.inJustDecodeBounds = false

    return contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, options)
    }
}

private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val (height: Int, width: Int) = options.outHeight to options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}

/**
 * 비율을 유지하며 최대 해상도에 맞게 리사이징
 */
private fun resizeBitmap(bitmap: Bitmap, reqWidth: Int, reqHeight: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height

    val scale = (reqWidth.toFloat() / width).coerceAtMost(reqHeight.toFloat() / height)

    // 이미 기준보다 작다면 원본 반환
    if (scale >= 1.0f) return bitmap

    val newWidth = (width * scale).toInt()
    val newHeight = (height * scale).toInt()

    return bitmap.scale(newWidth, newHeight)
}