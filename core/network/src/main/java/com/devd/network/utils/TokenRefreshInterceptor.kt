//package com.devd.network.utils
//
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.runBlocking
//import okhttp3.Interceptor
//import okhttp3.Request
//import okhttp3.Response
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import timber.log.Timber
//
//class TokenRefreshInterceptor(
//    private val url: String,
//    private val dataStoreRepository: DataStoreRepository
//
//) : Interceptor {
//
//    val tempToken =
//        "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyMDIiLCJOY25WdWVyb2lkQVBJMjAyMyI6IlJPTEVfVVNFUiIsImV4cCI6MTY5NzI3MDgyNX0.Bi1H3AMb7eshtzR5UKW7P6sMx-WVqK1esfdJ6PmK3MLGNLOSMLM8lT8A27z0M_7hhdaDqA-KogDfDeFvIHAb3A"
//
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val userData = runBlocking { dataStoreRepository.getDataObject<CloudUserData>(USER_DATA,CloudUserData::class.java) }
//        val response = chain.proceed(request(chain, userData?.token ?: tempToken))
//        if (response.code == 401 || response.code == 400) {
//            val reToken = userData?.rToken
//            Timber.d("intercept rToken : ${reToken}}")
//            if (reToken != null) {
//                val retrofit: Retrofit = Retrofit.Builder()
//                    .baseUrl(url)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build()
//                val api: CloudApiService =
//                    retrofit.create(CloudApiService::class.java)
//                val requestBody =
//                    api.getToken(RefreshTokenReq(reToken)).execute().body()
//                Timber.d("intercept requestBody1 : ${requestBody}}")
//                requestBody?.let {
//                    val res =
//                        Gson().fromJson<RefreshTokenRes>(
//                            requestBody.string(), object : TypeToken<RefreshTokenRes>() {}.type
//                        )
//                    Timber.d(" refreshToken : requestBody2 : $res")
//                    updateUserDataToken(
//                        res.token,
//                        res.refreshToken,
//                        res.distanceUnitYn,
//                        res.tempUnitYn
//                    )
//                    response.close()
//                    return chain.proceed(request(chain, res.token))
//                }
//            }
//        }
//        return response
//    }
//
//
//    private fun request(chain: Interceptor.Chain, token: String): Request {
//        return chain.request().newBuilder()
//            .header("Authorization", "Bearer $token")
//            .header("Accept", "application/json")
//            .header("Connection", "close")
//            .method(chain.request().method, chain.request().body)
//            .build()
//    }
//
//    private fun updateUserDataToken(
//        newToken: String,
//        rToken: String,
//        distance: String,
//        tempUnit: String
//    ) {
//        CoroutineScope(Dispatchers.IO).launch {
//            val preUserData = dataStoreRepository.getDataObject<CloudUserData>(
//                USER_DATA,
//                CloudUserData::class.java
//            ) ?: return@launch
//            preUserData.token = newToken
//            preUserData.rToken = rToken
//            preUserData.distanceUnit = distance
//            preUserData.tempUnit = tempUnit
//            dataStoreRepository.setDataObject(USER_DATA,preUserData)
//        }
//    }
//
//}