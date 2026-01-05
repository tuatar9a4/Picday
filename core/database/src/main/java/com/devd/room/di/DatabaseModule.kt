package com.devd.room.di

import android.content.Context
import com.devd.room.DiaryDatabase
import com.devd.room.dao.DiaryBookDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDiaryDatabase(@ApplicationContext context: Context): DiaryDatabase =
        DiaryDatabase.buildDatabase(context)


    @Provides
    fun provideDriveDao(historyDatabase: DiaryDatabase): DiaryBookDao =
        historyDatabase.diaryBookDao()
}