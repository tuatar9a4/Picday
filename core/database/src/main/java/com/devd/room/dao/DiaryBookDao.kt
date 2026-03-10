package com.devd.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.devd.room.entity.DiaryBookEntity

@Dao
interface DiaryBookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiaryBook(diaryBook: DiaryBookEntity)

    @Update
    suspend fun updateDiaryBook(diaryBook: DiaryBookEntity)

    @Query(
        """
        SELECT * FROM diary_book 
        WHERE userLocalUUId = :uuid AND isDeleted = 0 
        ORDER BY createdAt ASC
        """
    )
    suspend fun selectAllDiaryBook(uuid: String): List<DiaryBookEntity>

    @Query("SELECT * FROM diary_book WHERE userLocalUUId = :uuid AND isMajor = 1")
    suspend fun selectMainDiaryBook(uuid: String): DiaryBookEntity

    @Query("SELECT * FROM diary_book WHERE localId = :bookId")
    suspend fun selectDiaryBook(bookId: Long): DiaryBookEntity


    /* =====================
     * Delete (Soft Delete)
     * ===================== */

    @Query(
        """
        UPDATE diary_book
        SET isDeleted = 1,
            updatedAt = :deletedAt
        WHERE localId = :diaryBookId
    """
    )
    suspend fun softDeleteDiaryBook(
        diaryBookId: String,
        deletedAt: Long = System.currentTimeMillis()
    )
}