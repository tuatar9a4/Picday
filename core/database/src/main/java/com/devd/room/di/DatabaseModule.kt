package com.devd.room.di

import android.content.Context
import com.devd.room.DiaryDatabase
import com.devd.room.dao.DiaryBookDao
import com.devd.room.dao.DiaryDao
import com.devd.room.dao.DiaryImageDao
import com.devd.room.dao.DiaryTagDao
import com.devd.room.dao.TagDao
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
    fun provideDriveBookDao(historyDatabase: DiaryDatabase): DiaryBookDao =
        historyDatabase.diaryBookDao()

    @Provides
    fun provideDriveDao(historyDatabase: DiaryDatabase): DiaryDao =
        historyDatabase.diaryDao()

    @Provides
    fun provideDiaryImageDao(historyDatabase: DiaryDatabase): DiaryImageDao =
        historyDatabase.diaryImageDao()

    @Provides
    fun provideDiaryTagDao(historyDatabase: DiaryDatabase): DiaryTagDao =
        historyDatabase.diaryTagDao()

    @Provides
    fun provideTagDao(historyDatabase: DiaryDatabase): TagDao =
        historyDatabase.tagDao()
}