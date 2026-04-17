package com.devd.firebase.fcm

import com.google.firebase.Firebase
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.functions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import kotlin.coroutines.resume

object FcmExtension {


    suspend fun sendAlarmDataToFirebaseServer(data: HashMap<String, Any?>): Map<*, *>? {
        Timber.d("FCM_SERVER 서버 전송: ${data}")
        val functions: FirebaseFunctions = Firebase.functions("asia-northeast3")
        val result = functions
            .getHttpsCallable("registerUserToBucket")
            .call(data)
            .await() // Tasks-Kotlin 라이브러리 필요

        val responseData = result.data as? Map<*, *>
        Timber.d("FCM_SERVER 서버 응답 ${result.data}=> : ${responseData?.get("message")}")
        return responseData
    }

    suspend fun getFcmToken() = suspendCancellableCoroutine { continuation ->
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                return@addOnCompleteListener continuation.resume(null)
            }
            continuation.resume(it.result)
        }
    }
}