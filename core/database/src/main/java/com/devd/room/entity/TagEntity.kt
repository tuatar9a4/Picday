package com.devd.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tag")
data class TagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String
)


@Entity(
    tableName = "diary_tag_cross",
    primaryKeys = ["diaryId", "tagId"]
)
data class DiaryTagCrossEntity(
    val diaryId: Long,
    val tagId: Long
)
