package com.devd.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.devd.room.dao.DiaryBookDao
import timber.log.Timber
import java.util.concurrent.Executors

@Database(
    entities = [DiaryBookEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DiaryDatabase : RoomDatabase() {

    abstract fun diaryBookDao(): DiaryBookDao


    companion object {

        fun buildDatabase(context: Context): DiaryDatabase {
            return Room.databaseBuilder(
                context,
                DiaryDatabase::class.java,
                "local_diary_db"
            ).setQueryCallback(
                { sqlQuery, bindArgs -> Timber.tag("SQL LOG").d("$sqlQuery   $bindArgs") },
                Executors.newSingleThreadExecutor()
            ).addMigrations()
                .build()
        }
    }
}