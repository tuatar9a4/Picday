package com.devd.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

sealed class DataStoreKey<T>(
    val key: String
) {
    abstract fun preferencesKey(): Preferences.Key<T>

    object UserToken : DataStoreKey<String>("user_token") {
        override fun preferencesKey() = stringPreferencesKey(key)
    }

    object UserReToken : DataStoreKey<String>("user_re_token") {
        override fun preferencesKey() = stringPreferencesKey(key)
    }

    object UserInfo : DataStoreKey<String>("user_info") {
        override fun preferencesKey() = stringPreferencesKey(key)
    }

    object LocalSettingKey : DataStoreKey<String>("local_setting_Data") {
        override fun preferencesKey() = stringPreferencesKey(key)
    }

    object FontIndex : DataStoreKey<Int>("font_index") {
        override fun preferencesKey() = intPreferencesKey(key)
    }

    object FcmToken : DataStoreKey<String>("fcm_token") {
        override fun preferencesKey() = stringPreferencesKey(key)
    }

    object SavedAlarmTime : DataStoreKey<String>("saved_alarm_time") {
        override fun preferencesKey() = stringPreferencesKey(key)
    }

}

