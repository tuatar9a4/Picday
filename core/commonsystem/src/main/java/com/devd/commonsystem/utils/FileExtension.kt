package com.devd.commonsystem.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File

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

fun Context.uriToFile(uri: Uri): File {
    val resolver = contentResolver

//    val fileName = getFileName(uri) ?: "temp_image_${System.currentTimeMillis()}.jpg"
    val fileName = "temp_image_${System.currentTimeMillis()}.jpg"
    val file = File(cacheDir, fileName)

    resolver.openInputStream(uri)?.use { inputStream ->
        file.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }

    return file
}