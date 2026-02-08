package com.devd.room.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class DiaryWithExtras(
    @Embedded val diary: DiaryEntity,

    @Relation(
        parentColumn = "localId",
        entityColumn = "diaryId"
    )
    val images: List<DiaryImageEntity>,

    @Relation(
        parentColumn = "localId",
        entityColumn = "id",
        associateBy = Junction(
            value = DiaryTagCrossEntity::class,
            parentColumn = "diaryId",
            entityColumn = "tagId"
        )
    )
    val tags: List<TagEntity>
)
