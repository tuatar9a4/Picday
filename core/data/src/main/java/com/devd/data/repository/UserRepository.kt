package com.devd.data.repository

import com.devd.data.utils.CallResult
import com.devd.data.utils.SafeNetCall
import com.devd.model.remote.LoginRequest
import com.devd.model.remote.LoginResponse
import com.devd.model.remote.SignupRequest
import com.devd.model.remote.SignupResponse
import com.devd.network.di.NetworkModule
import com.devd.network.service.DiaryService
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class UserRepository @Inject constructor(
    @param:NetworkModule.DiaryServer private val diaryService: DiaryService,
) : SafeNetCall() {

    suspend fun checkCallTest() {
        safeApiCall(Dispatchers.IO) {
            diaryService.testConnectServer()
        }.run {
            when (this) {
                is CallResult.Success -> {
                    Timber.d("Check Success => ${this.res}")
                }

                is CallResult.NetworkError -> {
                    Timber.d("Check Fauk => ${this.throwable} => ${this.message}")

                }
            }
        }
    }

    suspend fun checkExistsId(id: String): Boolean {
        safeApiCall(Dispatchers.IO) {
            diaryService.checkExistsID(id)
        }.run {
            return this is CallResult.Success
        }
    }

    suspend fun requestLoginUser(id: String, pw: String): LoginResponse? {
        safeApiCall(Dispatchers.IO) {
            diaryService.loginUser(LoginRequest(id, pw))
        }.run {
            return when (this) {
                is CallResult.Success -> this.res
                is CallResult.NetworkError -> null
            }
        }
    }

    suspend fun registerNewId(
        email: String,
        password: String,
        nickname: String
    ): SignupResponse? {
        safeApiCall(Dispatchers.IO) {
            val uuid = UUID.randomUUID().toString()
            val requestBody = SignupRequest(
                email = email,
                password = password,
                uuid = uuid,
                nickname = nickname
            )
            diaryService.signupUser(requestBody)
        }.run {
            return when (this) {
                is CallResult.Success -> this.res
                is CallResult.NetworkError -> null
            }
        }
    }

    suspend fun checkSyncTime() {
//        safeApiCall(Dispatchers.IO){
//            diaryService.fetchSyncTime()
//        }
    }
}