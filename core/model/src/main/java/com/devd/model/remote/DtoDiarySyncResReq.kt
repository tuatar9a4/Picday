package com.devd.model.remote

// 안드로이드에서 서버로 일기 동기화(저장/수정)를 요청할 때
data class DiarySyncRequest(
    val localId: Long, // 앱의 Room PK (나중에 응답으로 돌려줌)
    val remoteId: Long?, // 이미 서버에 동기화 된 적 있으면 값이 있음
    val diaryBookRemoteId: Long, // 어느 일기장인지
    val content: String,
    val mood: Int?,
    val weather: Int?,
    val createdAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean,
    val images: List<ImageDto>,
    val tags: List<String> // 태그는 이름만 보내면 서버가 알아서 처리
) {
    data class ImageDto(val uri: String, val order: Int)
}

// 서버에서 앱으로 동기화 결과를 알려줄 때
data class SyncResponse(
    val localId: Long, // 앱에서 보냈던 로컬 ID
    val remoteId: Long // 서버에 저장된/수정된 서버 ID
)