package com.devd.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.devd.room.entity.DiaryEntity
import com.devd.room.entity.DiaryWithExtras

@Dao
interface DiaryDao {

    /* Create */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiary(diary: DiaryEntity) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiaries(diaries: List<DiaryEntity>)


    /* Update */

    @Update
    suspend fun updateDiary(diary: DiaryEntity)

    /* Read */

    @Query(
        """
        SELECT * FROM diary
        WHERE localId = :diaryId
        LIMIT 1
    """
    )
    suspend fun getDiaryById(diaryId: String): DiaryEntity?

    @Query(
        """
        SELECT * FROM diary
        WHERE diaryBookId = :diaryBookId AND isDeleted = 0
        ORDER BY createdAt DESC
    """
    )
    suspend fun getDiariesByDiaryBook(
        diaryBookId: String
    ): List<DiaryEntity>


    @Transaction
    @Query(
        """
        SELECT * FROM diary
        WHERE diaryBookId = :diaryBookId AND isDeleted = 0
        ORDER BY createdAt DESC
    """
    )
    suspend fun getDiariesWithExtras(
        diaryBookId: String
    ): List<DiaryWithExtras>

    @Query(
        """
        SELECT * FROM diary
        WHERE diaryBookId = :diaryBookId
          AND createdAt BETWEEN :from AND :to
          AND isDeleted = 0
        ORDER BY createdAt DESC
    """
    )
    suspend fun getDiariesByDateRange(
        diaryBookId: String,
        from: Long,
        to: Long
    ): List<DiaryEntity>

    /* Delete */

    @Query(
        """
        UPDATE diary
        SET isDeleted = 1,
            updatedAt = :deletedAt
        WHERE localId = :diaryId
    """)
    suspend fun softDeleteDiary(
        diaryId: String,
        deletedAt: Long = System.currentTimeMillis()
    )
}