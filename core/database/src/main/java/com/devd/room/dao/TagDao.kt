package com.devd.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.devd.room.entity.TagEntity

@Dao
interface TagDao {

    /* Create */

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: TagEntity): Long

    /* Read */

    @Query("""
        SELECT * FROM tag
        ORDER BY name ASC
    """)
    suspend fun getAllTags(): List<TagEntity>

    @Query("""
        SELECT * FROM tag
        WHERE name = :name
        LIMIT 1
    """)
    suspend fun getTagByName(name: String): TagEntity?


}