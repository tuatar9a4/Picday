package com.devd.network.di

import com.devd.network.BuildConfig
import com.devd.network.service.DiaryService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class DiaryServer

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class DiaryClient

    @DiaryServer
    @Provides
    fun provideDiaryServerUri() = "http://10.0.2.2:8080/"

    @DiaryClient
    @Singleton
    @Provides
    fun provideDiaryOkHttpClient(
//        @DiaryServer url: String,
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Timber.tag("OkHttpInterceptor").d(message)
        }.apply {
            if (BuildConfig.DEBUG) {
                setLevel(HttpLoggingInterceptor.Level.BASIC)
            } else {
                setLevel(HttpLoggingInterceptor.Level.NONE)
            }
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
//            .addInterceptor(TokenRefreshInterceptor(url, dataStoreRepository))
            .build()
    }

    @DiaryClient
    @Singleton
    @Provides
    fun provideDiaryRetrofit(
        @DiaryServer url: String,
        @DiaryClient client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl(url)
            .build()
    }

    @DiaryServer
    @Singleton
    @Provides
    fun provideDiaryService(
        @DiaryClient retrofit: Retrofit
    ): DiaryService = retrofit.create(DiaryService::class.java)

}