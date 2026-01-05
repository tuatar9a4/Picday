package com.devd.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey

sealed class DataStoreKey<T>(
    val key: String
) {
    abstract fun preferencesKey(): Preferences.Key<T>

    object UserNickName : DataStoreKey<String>("user_nick_name") {
        override fun preferencesKey() = stringPreferencesKey(key)

    }


}
