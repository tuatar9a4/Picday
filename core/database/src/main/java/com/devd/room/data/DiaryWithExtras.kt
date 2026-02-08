package com.devd.room.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.devd.model.local.DiaryInfo
import com.devd.room.entity.DiaryEntity
import com.devd.room.entity.DiaryImageEntity
import com.devd.room.entity.DiaryTagCrossEntity
import com.devd.room.entity.TagEntity

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
) {
    fun transToModel() =
        DiaryInfo(
            diaryId = diary.localId,
            diaryBookId = diary.diaryBookId,
            content = diary.content,
            mood = diary.mood,
            weather = diary.weather,
            createdAt = diary.createdAt,
            updatedAt = diary.updatedAt,
            imageUrlList = images.map { it.uri },
            tagList = tags.map { it.name },
        )

}