package com.devd.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.devd.model.local.DiaryBookInfo

@Entity(
    tableName = "diary_books",
    indices = [Index("userLocalUUId")]
)
data class DiaryBookEntity(

    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0L,

    val remoteID: Long? = null,

    val userLocalUUId: String? = null,
    val title: String,        // 맛집 일기장, 여행 일기장 등
    val description: String?,

    val isMain: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long?
) {
    fun transToModel() = DiaryBookInfo(
        bookId = localId,
        title = title,
        description = description
    )
}