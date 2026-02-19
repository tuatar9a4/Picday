package com.devd.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.devd.room.entity.DiaryImageEntity

@Dao
interface DiaryImageDao {
    /* Create */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: DiaryImageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<DiaryImageEntity>)

    /* Read */

    @Query("""
        SELECT * FROM diary_image
        WHERE diaryId = :diaryId
        ORDER BY `order` ASC
    """)
    suspend fun getImagesByDiary(diaryId: Long): List<DiaryImageEntity>

    /* Delete */

    @Query("""
        DELETE FROM diary_image
        WHERE diaryId = :diaryId
    """)
    suspend fun deleteImagesByDiary(diaryId: Long)

    @Query("""
        DELETE FROM diary_image
        WHERE id = :imageId
    """)
    suspend fun deleteImage(imageId: String)

}