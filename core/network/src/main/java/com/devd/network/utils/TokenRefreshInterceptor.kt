package com.devd.network.utils

import com.devd.datastore.DataStoreKey
import com.devd.datastore.DataStoreRepository
import com.devd.model.remote.RefreshRequest
import com.devd.network.service.DiaryService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class TokenRefreshInterceptor(
    private val url: String,
    private val dataStoreRepository: DataStoreRepository,
) : Interceptor {

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val tempToken =
        "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyMDIiLCJOY25WdWVyb2lkQVBJMjAyMyI6IlJPTEVfVVNFUiIsImV4cCI6MTY5NzI3MDgyNX0.Bi1H3AMb7eshtzR5UKW7P6sMx-WVqK1esfdJ6PmK3MLGNLOSMLM8lT8A27z0M_7hhdaDqA-KogDfDeFvIHAb3A"

    override fun intercept(chain: Interceptor.Chain): Response {
        val (userToken, userReToken) = runBlocking {
            val token = dataStoreRepository.getPreferData(DataStoreKey.UserToken)
            val reToken = dataStoreRepository.getPreferData(DataStoreKey.UserReToken)
            token to reToken
        }
        val response = chain.proceed(request(chain, userToken ?: tempToken))
        if (response.code == 401 || response.code == 400) {
            Timber.d("intercept rToken : ${userReToken}}")
            if (userReToken != null) {
                val api: DiaryService = retrofit.create(DiaryService::class.java)
                val refreshResponse = runBlocking {
                    try {
                        api.tokenRefresh(RefreshRequest(userReToken))
                    } catch (e: Exception) {
                        null
                    }
                }
                Timber.d("intercept requestBody1 : ${refreshResponse}}")
                refreshResponse?.let {
                    updateUserDataToken(
                        refreshResponse.accessToken,
                        refreshResponse.refreshToken
                    )
                    response.close()
                    return chain.proceed(request(chain,refreshResponse.accessToken))
                }
            }
        }
        return response
    }


    private fun request(chain: Interceptor.Chain, token: String): Request {
        return chain.request().newBuilder()
            .header("Authorization", "Bearer $token")
            .header("Accept", "application/json")
            .header("Connection", "close")
            .method(chain.request().method, chain.request().body)
            .build()
    }

    private fun updateUserDataToken(newToken: String, rToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            dataStoreRepository.setPreferData(DataStoreKey.UserToken,newToken)
            dataStoreRepository.setPreferData(DataStoreKey.UserReToken,rToken)
        }
    }

}