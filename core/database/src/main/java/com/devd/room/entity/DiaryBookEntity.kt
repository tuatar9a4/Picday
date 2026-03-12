package com.devd.room.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.devd.model.local.DiaryBookInfo
import com.devd.model.local.DiaryPhaseType

@Entity(
    tableName = "diary_book",
    indices = [Index("userLocalUUId")]
)
data class DiaryBookEntity(

    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0L,

    val remoteID: Long? = null,

    val userLocalUUId: String? = null,

    val bookImage: String,
    val title: String,        // 맛집 일기장, 여행 일기장 등
    val description: String?,
    val bookPhaseType: Int = 0,

    val isMajor: Boolean = false,

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
        bookPhaseType = DiaryPhaseType.entries[bookPhaseType],
        isMajor = isMajor,
    )
}
