package com.devd.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "diary_books",
    indices = [Index("userId")]
)
data class DiaryBookEntity(

    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0L,

    val remoteID: Long? = null,

    val userId: Long,
    val title: String,        // 맛집 일기장, 여행 일기장 등
    val description: String?,

    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)