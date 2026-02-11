package com.devd.data.di

import android.content.Context
import com.devd.data.R
import com.devd.data.repository.DiaryBookRepository
import com.devd.data.repository.OracleRepository
import com.devd.data.repository.UserRepository
import com.devd.network.di.NetworkModule
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
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class OciKey

    @OciKey
    @Singleton
    @Provides
    fun provideOciKeyResolver(@ApplicationContext context: Context): String =
        context.getString(R.string.ociBuketKey)

    @Singleton
    @Provides
    fun provideDiaryBookRepository(
        diaryBookDao: DiaryBookDao,
        diaryDao: DiaryDao,
        diaryImageDao: DiaryImageDao,
        diaryTagDao: DiaryTagDao,
        tagDao: TagDao,
    ): DiaryBookRepository = DiaryBookRepository(
        diaryBookDao,
        diaryDao,
        diaryImageDao,
        diaryTagDao,
        tagDao
    )

    @Singleton
    @Provides
    fun provideOracleRepository(
        @OciKey ociKey: String,
        @NetworkModule.OciServer oracleService: OracleService,
    ): OracleRepository = OracleRepository(
        ociBuketKey = ociKey,
        oracleService = oracleService
    )

    @Singleton
    @Provides
    fun provideUserRepository(
        @NetworkModule.DiaryServer diaryService: DiaryService
    ): UserRepository = UserRepository(
        diaryService

    )


}