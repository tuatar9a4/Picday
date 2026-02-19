package com.devd.editor.data

import android.net.Uri


sealed class ImageType
data class Local(val uri: Uri? = null) : ImageType()
data class Remote(val url: String? = null) : ImageType()

data class DiaryInfoState(
    var bookId: Long,
    var diaryId: Long? = null,
    var imageUrl: ImageType? = null,
    var diaryContents: String = "",
    var diaryTag: List<String> = listOf()
)