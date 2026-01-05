package com.devd.data.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

open class SafeNetCall {

    suspend fun <T> safeApiCall(
        dispatcher: CoroutineDispatcher,
        apiCall: suspend () -> T
    ): CallResult<T> {
        return withContext(dispatcher) {
            try {
                CallResult.Success(apiCall.invoke())
            } catch (throwable: Throwable) {

                when (throwable) {
                    is IOException -> {
                        Timber.d("IOException occurred \n ErrorCode: ${throwable.message}")
                        CallResult.NetworkError(throwable.message.toString(),throwable)
                    }

                    is HttpException -> {
                        val httpCode = throwable.code().toString()
                        val errorApi = throwable.response()?.raw()?.request?.url?.toString()
                        Timber.d("HttpException occurred \n ErrorCode : ${httpCode} \n ErrorAPI : ${errorApi} \n ErrorMessage : ${throwable.message}")
                        CallResult.NetworkError(httpCode)
                    }

                    else -> {
                        Timber.d("Unknown Exception occurred \n ErrorMessage : ${throwable.message}")
                        CallResult.NetworkError(throwable.message.toString())
                    }
                }
            }
        }
    }
}


sealed class CallResult<out T> {
    data class Success<out T>(val res: T) : CallResult<T>()
    data class NetworkError(val message: String,val throwable: Throwable? = null) : CallResult<Nothing>()
}