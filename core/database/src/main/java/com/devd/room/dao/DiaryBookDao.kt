package com.devd.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.devd.model.local.DiaryBookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryBookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiaryBook(diaryBook: DiaryBookEntity)

    @Update
    suspend fun updateDiaryBook(diaryBook: DiaryBookEntity)

    @Query(
        """
        SELECT * FROM diary_book 
        WHERE userUuid = :uuid AND isDeleted = 0 
        ORDER BY createdAt ASC
        """
    )
    suspend fun selectAllDiaryBook(uuid: String): List<DiaryBookEntity>

    @Query(
        """
    SELECT * FROM diary_book 
    WHERE userUuid = :uuid AND isDeleted = 0 
    ORDER BY createdAt ASC
    """
    )
    fun selectAllDiaryBookFlow(uuid: String): Flow<List<DiaryBookEntity>>

    @Query(
        """
        SELECT * FROM diary_book 
        WHERE userUuid = :uuid AND remoteID is NULL AND isDeleted = 0
        ORDER BY createdAt ASC
        """
    )
    suspend fun selectAllNotSyncDiaryBook(uuid: String): List<DiaryBookEntity>

    @Query("SELECT * FROM diary_book WHERE userUuid = :uuid AND isMajor = 1 AND isDeleted = 0")
    suspend fun selectMainDiaryBook(uuid: String): DiaryBookEntity

    @Query("SELECT * FROM diary_book WHERE localId = :bookId AND isDeleted = 0")
    suspend fun selectDiaryBook(bookId: Long): DiaryBookEntity

    @Query("UPDATE diary_book SET remoteID = :remoteID ,updatedAt = :updateAt WHERE localId = :localId")
    suspend fun updateRemoteId(remoteID: Long, localId: Long,updateAt : Long = System.currentTimeMillis())

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