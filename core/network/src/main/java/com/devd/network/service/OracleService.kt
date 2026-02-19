package com.devd.network.service

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Streaming

interface OracleService {

    @Streaming
    @PUT("p/{key}/n/cnud835pjoeg/b/devd_storage/o/diary/{header}/{filename}")
    suspend fun uploadFile(
        @Path("key") key: String,
        @Path("header") header: String,
        @Path("filename") fileName: String,
        @Body file: RequestBody
    )



    @DELETE("p/{key}/n/cnud835pjoeg/b/devd_storage/o/diary/{header}/{filename}")
    suspend fun deleteFile(
        @Path("key") key: String,
        @Path("header") header: String,
        @Path("filename") fileName: String,
    )
}