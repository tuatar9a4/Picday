package com.devd.model.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "diary_book",
    indices = [Index("userUuid")]
)
data class DiaryBookEntity(

    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0L,

    val remoteID: Long? = null,

    val userUuid: String? = null,

    val bookImage: String,
    val title: String,        // 맛집 일기장, 여행 일기장 등
    val description: String?,
    val bookPhaseType: Int = 0,
    val bookColor: Int = 0,

    val isMajor: Boolean = false,

    val conWriteCount: Long = 0,

    val createdAt: Long,
    val updatedAt: Long?,

    val isDeleted: Boolean = false
) {
    fun transToModel() = DiaryBookInfo(
        bookId = localId,
        bookImage = bookImage,
        title = title,
        description = description,
        createDate = createdAt,
        continueWriteCount = conWriteCount,
        bookPhaseType = DiaryPhaseType.entries[bookPhaseType],
        bookColor = bookColor,
        isMajor = isMajor,
    )
}