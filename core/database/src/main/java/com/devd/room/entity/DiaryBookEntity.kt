package com.devd.room.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.devd.model.local.DiaryBookInfo

@Entity(
    tableName = "diary_book",
    indices = [Index("userLocalUUId")]
)
data class DiaryBookEntity(

    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0L,

    val remoteID: Long? = null,

    val userLocalUUId: String? = null,
    val title: String,        // 맛집 일기장, 여행 일기장 등
    val description: String?,

    val isMajor: Boolean = false,

    val createdAt: Long,
    val updatedAt: Long?,

    val isDeleted : Boolean = false
) {
    fun transToModel() = DiaryBookInfo(
        bookId = localId,
        title = title,
        description = description
    )
}