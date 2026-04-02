package com.devd.network.service

import com.devd.model.remote.DiaryBatchSyncReq
import com.devd.model.remote.DiaryBatchSyncRes
import com.devd.model.remote.DiaryBookBatchSyncReq
import com.devd.model.remote.DiaryBookBatchSyncRes
import com.devd.model.remote.LoginRequest
import com.devd.model.remote.LoginResponse
import com.devd.model.remote.RefreshRequest
import com.devd.model.remote.SignupRequest
import com.devd.model.remote.SignupResponse
import com.devd.model.remote.TokenResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface DiaryService {

    @GET("test/call")
    suspend fun testConnectServer()


    @GET("user/check/id")
    suspend fun checkExistsID(@Query("id") id: String)

    @GET("auth/checkToken")
    suspend fun checkToken(@Header("Authorization") token: String)

    @POST("user/signup")
    suspend fun signupUser(@Body request: SignupRequest): SignupResponse

    @POST("auth/login")
    suspend fun loginUser(@Body request: LoginRequest): LoginResponse

    @POST("auth/refresh")
    suspend fun tokenRefresh(@Body request: RefreshRequest): TokenResponse

    @POST("api/v1/diary-books/sync/batch")
    suspend fun syncDiaryBooksBatch(
        @Body request: DiaryBookBatchSyncReq
    ): DiaryBookBatchSyncRes

    @POST("api/v1/diaries/sync/batch")
    suspend fun syncDiariesBatch(
        @Body request: DiaryBatchSyncReq
    ): DiaryBatchSyncRes

}