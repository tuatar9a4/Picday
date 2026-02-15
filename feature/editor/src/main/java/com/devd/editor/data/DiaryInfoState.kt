package com.devd.editor.data

import android.net.Uri

data class DiaryInfoState(
    var bookId: Long,
    var diaryId: Long? = null,
    var imageUrl: Uri? = null,
    var diaryContents: String = "",
    var diaryTag: List<String> = listOf()
)