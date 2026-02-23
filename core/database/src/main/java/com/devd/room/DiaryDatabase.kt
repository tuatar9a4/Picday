package com.devd.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.devd.room.dao.DiaryBookDao
import com.devd.room.dao.DiaryDao
import com.devd.room.dao.DiaryImageDao
import com.devd.room.dao.DiaryTagDao
import com.devd.room.dao.TagDao
import com.devd.room.entity.DiaryBookEntity
import com.devd.room.entity.DiaryEntity
import com.devd.room.entity.DiaryImageEntity
import com.devd.room.entity.DiaryTagCrossEntity
import com.devd.room.entity.TagEntity
import timber.log.Timber
import java.util.concurrent.Executors

@Database(
    entities = [DiaryBookEntity::class, DiaryEntity::class, DiaryImageEntity::class, TagEntity::class, DiaryTagCrossEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DiaryDatabase : RoomDatabase() {

    abstract fun diaryBookDao(): DiaryBookDao
    abstract fun diaryDao(): DiaryDao
    abstract fun diaryImageDao(): DiaryImageDao
    abstract fun diaryTagDao(): DiaryTagDao
    abstract fun tagDao(): TagDao


    companion object {

        fun buildDatabase(context: Context): DiaryDatabase {
            return Room.databaseBuilder(
                context,
                DiaryDatabase::class.java,
                "local_diary_db"
            ).setQueryCallback(
                { sqlQuery, bindArgs -> Timber.tag("SQL_LOG").d("$sqlQuery   $bindArgs") },
                Executors.newSingleThreadExecutor()
            ).addMigrations()
                .build()
        }
    }
}