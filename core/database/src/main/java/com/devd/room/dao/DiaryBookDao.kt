package com.devd.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.devd.room.DiaryBookEntity

@Dao
interface DiaryBookDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertDiaryBook(diaryBook: DiaryBookEntity): Long

    @Query("SELECT * FROM diary_books WHERE userLocalUUId = :uuid")
    suspend fun selectAllDiaryBook(uuid: String): List<DiaryBookEntity>

    @Query("SELECT * FROM diary_books WHERE isMain = :isMain")
    suspend fun selectMainDiaryBook(isMain : Boolean): List<DiaryBookEntity>

}