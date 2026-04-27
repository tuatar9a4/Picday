package com.devd.network.service

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Streaming
import retrofit2.http.Url

interface OracleService {

    @Streaming
    @PUT
    suspend fun uploadFile(
        @Url url :String,
        @Body file: RequestBody
    )

}