package com.devd.firebase.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.devd.datastore.DataStoreKey
import com.devd.datastore.DataStoreRepository
import com.devd.firebase.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DiaryFirebaseMessageService : FirebaseMessagingService() {

    @Inject lateinit var dataStoreRepository : DataStoreRepository

    override fun onMessageReceived(message: RemoteMessage) {
        Timber.d("CheckMessage => ${message.data}")
        Timber.d("CheckMessage => ${message.notification}")
        Timber.d("CheckMessage => ${message.notification?.title}")
        Timber.d("CheckMessage => ${message.notification?.body}")
        message.notification?.body?.let { sendNotification(it) }
    }

    override fun onNewToken(token: String) {
        Timber.d("CheckToken : onNewToken => $token")
        CoroutineScope(Dispatchers.IO).launch {
            val formatter = DateTimeFormatter.ofPattern("HHmm")
            val savedTime = dataStoreRepository.getPreferData(DataStoreKey.SavedAlarmTime)
            val savedTimeToUtc = savedTime?.let {
                val savedTime = LocalTime.of(
                    it.take(2).toIntOrNull() ?: 0,
                    it.takeLast(2).toIntOrNull() ?: 0
                )
                val localZonedSavedTime =
                    ZonedDateTime.now(ZoneId.systemDefault()).with(savedTime)
                localZonedSavedTime.withZoneSameInstant(ZoneId.of("UTC"))
            }
            val data: HashMap<String, Any?> = hashMapOf(
                "fcmToken" to token,
                "utcTimeBucket" to savedTimeToUtc?.format(formatter),
                "previousBucket" to null
            )
            FcmExtension.sendAlarmDataToFirebaseServer(data)
        }
    }

    private fun sendNotification(messageBody: String) {
        // 알림 클릭 시 이동할 액티비티 지정 (Compose를 쓰신다면 보통 MainActivity가 됩니다)
        val intent = Intent()
        intent.setClassName(this, "com.devd.picday.MainActivity").apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        // Android 12(API 31) 이상에서는 FLAG_IMMUTABLE 또는 FLAG_MUTABLE을 필수로 지정해야 합니다.
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = "fcm_default_channel" // 알림 채널 ID
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.app_icon_stroke)
            .setContentText(messageBody)
            .setAutoCancel(true) // 터치 시 자동으로 알림 지우기
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android 8.0(Oreo, API 26) 이상에서는 알림 채널 생성이 필수입니다.
        val channel = NotificationChannel(
            channelId,
            "기본 알림 채널", // 사용자에게 보이는 채널 이름
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "FCM 기본 알림을 수신하는 채널입니다."
        }
        notificationManager.createNotificationChannel(channel)

        // 알림 표시 (동일한 ID를 쓰면 알림이 덮어씌워지고, 고유 ID를 쓰면 개별로 쌓입니다. 여기서는 고유한 타임스탬프를 예시로 씁니다.)
        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}