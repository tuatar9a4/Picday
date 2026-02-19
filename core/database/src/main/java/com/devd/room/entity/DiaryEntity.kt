package com.devd.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "diary",
    foreignKeys = [
        ForeignKey(
            entity = DiaryBookEntity::class,
            parentColumns = ["localId"],
            childColumns = ["diaryBookId"],
            onDelete = CASCADE
        )
    ],
    indices = [Index("diaryBookId"), Index("createdAt")]
)
data class DiaryEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0L,

    val remoteId: Long? = null,

    var diaryBookId: Long,     // ⭐ 어느 일기장에 속하는지

    var content: String,

    val createdAt: Long,
    var updatedAt: Long,

    val mood: Int? = null,
    val weather: Int? = null,

    val isDeleted: Boolean = false
)