package com.devd.model.remote

import com.devd.model.local.DiaryBookEntity
import com.google.gson.annotations.SerializedName

data class DiaryBookBatchSyncReq(
    @SerializedName("diaryBooks")
    val diaryBooks: List<DiaryBookEntity>
)

data class DiaryBookBatchSyncRes(
    @SerializedName("syncedItems")
    val syncedItems: List<DiaryResId>
)

data class DiaryResId(
    @SerializedName("localId")
    val localId: Long,
    @SerializedName("remoteId")
    val remoteId: Long
)