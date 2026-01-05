package com.devd.data.repository

import com.devd.data.utils.CallResult
import com.devd.data.utils.SafeNetCall
import com.devd.network.di.NetworkModule
import com.devd.network.service.DiaryService
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject

class UserRepository @Inject constructor(
    @NetworkModule.DiaryServer private val diaryService: DiaryService,
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
}