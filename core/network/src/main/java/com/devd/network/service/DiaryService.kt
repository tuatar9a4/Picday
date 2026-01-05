package com.devd.network.service

import retrofit2.http.GET
import retrofit2.http.Query

interface DiaryService {

    @GET("test/call")
    suspend fun testConnectServer()


    @GET("user/check/id")
    suspend fun checkExistsID(@Query("id") id: String)
}