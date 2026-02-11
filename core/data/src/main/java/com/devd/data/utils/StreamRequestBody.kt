package com.devd.data.utils

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream

class StreamRequestBody(
    private val file: File,
    private val contentType: String,
    private val uploadProgress: (Int) -> Unit
) : RequestBody() {

    override fun contentType(): MediaType? = contentType.toMediaTypeOrNull()

    override fun contentLength(): Long = file.length()

    override fun writeTo(sink: BufferedSink) {
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val totalUploadBytes = contentLength()
        var uploadBytes = 0L
        var beforeProgress = 0
        FileInputStream(file).use { inputStream ->
            var read: Int
            while (inputStream.read(buffer).also { read = it } != -1) {
                uploadBytes += read
                sink.write(buffer, 0, read)
                if (beforeProgress != (uploadBytes.toFloat() * 100 / totalUploadBytes).toInt()) {
                    beforeProgress = (uploadBytes.toFloat() * 100 / totalUploadBytes).toInt()
                    uploadProgress(beforeProgress)
                }
            }
        }
        sink.flush()
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 4 * 1024 * 1024 // 조절 가능
    }
}