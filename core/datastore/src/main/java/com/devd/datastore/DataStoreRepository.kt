package com.devd.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.lang.reflect.Type
import javax.inject.Inject


class DataStoreRepository @Inject constructor(
    private val sharedPreferences: DataStore<Preferences>
) {

    suspend fun setPreferString(key: String, value: String) : Boolean {
        val result = Result.runCatching {
            sharedPreferences.edit { preferences ->
                preferences[stringPreferencesKey(key)] = value
            }
        }
        return result.isSuccess
    }

    suspend fun getPreferString(key: String): String? {
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
                    preferences[stringPreferencesKey(key)]
                }
            val value = flow.firstOrNull()
            value
        }.getOrNull()
    }

    suspend fun getStringClear(key: String) {
        Result.runCatching {
            sharedPreferences.edit { preferences ->
                preferences.remove(stringPreferencesKey(key))
            }
        }
    }

    suspend fun setPreferLong(key: String, value: Long): Boolean {
        val result = Result.runCatching {
            sharedPreferences.edit { preferences ->
                preferences[longPreferencesKey(key)] = value
            }
        }
        return result.isSuccess
    }

    suspend fun getPreferLong(key: String): Long? {
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
                    preferences[longPreferencesKey(key)]
                }
            val value = flow.firstOrNull()
            value
        }.getOrNull()
    }

    suspend fun getLongClear(key: String) {
        Result.runCatching {
            sharedPreferences.edit { preferences ->
                preferences.remove(longPreferencesKey(key))
            }
        }
    }

    suspend fun setPreferBoolean(key: String, value: Boolean) {
        Result.runCatching {
            sharedPreferences.edit { preferences ->
                preferences[booleanPreferencesKey(key)] = value
            }
        }
    }

    suspend fun getPreferBoolean(key: String): Boolean? {
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
                    preferences[booleanPreferencesKey(key)]
                }
            val value = flow.firstOrNull()
            value
        }.getOrNull()
    }

    suspend fun getBooleanClear(key: String) {
        Result.runCatching {
            sharedPreferences.edit { preferences ->
                preferences.remove(stringPreferencesKey(key))
            }
        }
    }

    suspend fun <T> setDataObject(keyValue: String, objectData: T) {
        val json = Gson().toJson(objectData)
        sharedPreferences.edit { preferences ->
            preferences[stringPreferencesKey(keyValue)] = json
        }
    }

    suspend fun <T> getDataObject(keyValue: String, objType: Type): T? {
        val strObj = getPreferString(keyValue)
        return try {
            Gson().fromJson(strObj, objType)
        }catch (e :Exception){
            null
        }
    }

    suspend fun <T> getDataArrayList(keyValue: String, arrayListType: Type): List<T> {
        val json = getPreferString(keyValue)
        if (json.isNullOrBlank()) return listOf()

        return try {
            Gson().fromJson(json, arrayListType)
        } catch (e: java.io.IOException) {
            e.printStackTrace()
            listOf()
        }
    }

    suspend fun <T> setDataArrayList(keyValue: String, stringList: MutableList<T>) {
        try {
            val json: String = Gson().toJson(stringList)
            setPreferString(keyValue, json)
        } catch (e: java.io.IOException) {
        }
    }

}