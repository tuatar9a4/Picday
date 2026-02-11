package com.devd.network.di

import com.devd.network.BuildConfig
import com.devd.network.service.DiaryService
import com.devd.network.service.OracleService
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
    annotation class OciServer

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class DiaryClient

    @DiaryServer
    @Provides
    fun provideDiaryServerUri() = "http://10.0.2.2:8080/"

    @OciServer
    @Provides
    fun provideOciServerUri() =
        "https://cnud835pjoeg.objectstorage.ap-seoul-1.oci.customer-oci.com/"

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

    @DiaryServer
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

    @OciServer
    @Singleton
    @Provides
    fun provideOciRetrofit(
        @OciServer url: String,
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
        @DiaryServer retrofit: Retrofit
    ): DiaryService = retrofit.create(DiaryService::class.java)

    @OciServer
    @Singleton
    @Provides
    fun provideOciService(
        @OciServer retrofit: Retrofit
    ): OracleService = retrofit.create(OracleService::class.java)

}