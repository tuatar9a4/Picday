package com.devd.network.service

import com.devd.model.remote.LoginRequest
import com.devd.model.remote.LoginResponse
import com.devd.model.remote.SignupRequest
import com.devd.model.remote.SignupResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DiaryService {

    @GET("test/call")
    suspend fun testConnectServer()


    @GET("user/check/id")
    suspend fun checkExistsID(@Query("id") id: String)

    @POST("user/signup")
    suspend fun signupUser(@Body request: SignupRequest) : SignupResponse

    @POST("auth/login")
    suspend fun loginUser(@Body request: LoginRequest) : LoginResponse

}