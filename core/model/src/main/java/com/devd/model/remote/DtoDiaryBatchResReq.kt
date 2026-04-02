package com.devd.model.remote


// 안드로이드 -> 서버 (여러 개를 한 번에 보냄)
data class DiaryBatchSyncReq(
    val diaries: List<DiarySyncRequest>
)

// 서버 -> 안드로이드 (각각의 로컬 ID가 어떤 서버 ID를 부여받았는지 리스트로 반환)
data class DiaryBatchSyncRes(
    val syncedItems: List<SyncResponse>
)