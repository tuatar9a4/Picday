package com.devd.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.devd.room.entity.DiaryTagCrossEntity

@Dao
interface DiaryTagDao {

    /* Create */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCross(cross: DiaryTagCrossEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrosses(crosses: List<DiaryTagCrossEntity>)

    /* Delete */

    @Query("""
        DELETE FROM diary_tag_cross
        WHERE diaryId = :diaryId
    """)
    suspend fun deleteByDiary(diaryId: Long)
}