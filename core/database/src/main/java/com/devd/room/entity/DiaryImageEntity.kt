package com.devd.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "diary_image",
    foreignKeys = [
        ForeignKey(
            entity = DiaryEntity::class,
            parentColumns = ["localId"],
            childColumns = ["diaryId"],
            onDelete = CASCADE
        )
    ],
    indices = [Index("diaryId")]
)
data class DiaryImageEntity(
    @PrimaryKey val id: String,
    val diaryId: String,
    val uri: String,        // 로컬 URI or S3 URL
    val order: Int          // 이미지 순서
)