package com.devd.datastore

interface DatastoreRepositoryImpl {

    suspend fun <T> setPreferData(data: DataStoreKey<T>, value: T) : Boolean
    suspend fun <T> getPreferData(data: DataStoreKey<T>) : T?

    suspend fun <T> clearPreferData(data : DataStoreKey<T>)

}