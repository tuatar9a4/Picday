package com.devd.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.devd.datastore.DataStoreRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = BuildConfig.LIBRARY_PACKAGE_NAME)

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {

    @Binds
    @Singleton
    abstract fun bindDiaryPreferencesRepository(
        dataStoreRepository: DataStoreRepository
    ): DataStoreRepository

    companion object {
        @Provides
        @Singleton
        fun provideDiaryDataStore(
            @ApplicationContext context: Context
        ): DataStore<Preferences> {
            return context.dataStore
        }
    }
}
