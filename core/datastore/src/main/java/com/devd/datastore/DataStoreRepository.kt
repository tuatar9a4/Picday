package com.devd.datastore

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.devd.model.local.LocalSettingData
import com.devd.model.local.UserInfo
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepository @Inject constructor(
    val sharedPreferences: DataStore<Preferences>
) : DatastoreRepositoryImpl {

    val currentFontInfo: MutableState<Int> = mutableIntStateOf(0)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            currentFontInfo.value = getLocalSettingData().fontIndexInt
        }
    }

    override suspend fun <T> setPreferData(data: DataStoreKey<T>, value: T): Boolean {
        val result = Result.runCatching {
            sharedPreferences.edit { preferences ->
                preferences[data.preferencesKey()] = value
            }
        }
        return result.isSuccess
    }

    override suspend fun <T> getPreferData(data: DataStoreKey<T>): T? {
        return Result.runCatching {
            val flow = sharedPreferences.data
                .catch { exception ->
                    if (exception is IOException) {
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }
                .map { preferences ->
                    preferences[data.preferencesKey()]
                }
            val value = flow.firstOrNull()
            value
        }.getOrNull()
    }

    suspend fun setUserInfo(userInfo: UserInfo): UserInfo {
        val jsonSting = Gson().toJson(userInfo).toString()
        setPreferData(DataStoreKey.UserInfo, jsonSting)
        return userInfo
    }

    suspend fun getUserInfo(): UserInfo? {
        val jsonStr = getPreferData(DataStoreKey.UserInfo) ?: return null
        val item = Gson().fromJson(jsonStr, UserInfo::class.java)
        return item
    }

    suspend fun setLocalSettingData(settingData: LocalSettingData): LocalSettingData {
        val jsonSting = Gson().toJson(settingData).toString()
        currentFontInfo.value = settingData.fontIndexInt
        setPreferData(DataStoreKey.LocalSettingKey, jsonSting)
        return settingData
    }

    suspend fun getLocalSettingData(): LocalSettingData {
        val jsonStr = getPreferData(DataStoreKey.LocalSettingKey) ?: return LocalSettingData()
        val item = Gson().fromJson(jsonStr, LocalSettingData::class.java)
        return item
    }

}