package com.devd.data.di

import com.devd.data.repository.DiaryBookRepository
import com.devd.data.repository.OracleRepository
import com.devd.data.repository.UserRepository
import com.devd.network.di.NetworkModule
import com.devd.network.di.NetworkModule.DiaryServer
import com.devd.network.service.DiaryService
import com.devd.network.service.OracleService
import com.devd.room.dao.DiaryBookDao
import com.devd.room.dao.DiaryDao
import com.devd.room.dao.DiaryImageDao
import com.devd.room.dao.DiaryTagDao
import com.devd.room.dao.TagDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {


    @Singleton
    @Provides
    fun provideDiaryBookRepository(
        @DiaryServer diaryService: DiaryService,
        diaryBookDao: DiaryBookDao,
        diaryDao: DiaryDao,
        diaryImageDao: DiaryImageDao,
        diaryTagDao: DiaryTagDao,
        tagDao: TagDao,
    ): DiaryBookRepository = DiaryBookRepository(
        diaryService,
        diaryBookDao,
        diaryDao,
        diaryImageDao,
        diaryTagDao,
        tagDao
    )

    @Singleton
    @Provides
    fun provideOracleRepository(
        @NetworkModule.OciServer oracleService: OracleService,
        @NetworkModule.DiaryServer diaryService: DiaryService,
    ): OracleRepository = OracleRepository(
        oracleService = oracleService,
        diaryService = diaryService
    )

    @Singleton
    @Provides
    fun provideUserRepository(
        @NetworkModule.DiaryServer diaryService: DiaryService
    ): UserRepository = UserRepository(
        diaryService

    )


}