package com.devd.picday

import android.app.Application
import android.util.Log
import com.devd.datastore.DataStoreKey
import com.devd.datastore.DataStoreRepository
import com.devd.firebase.fcm.FcmExtension
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class OneDayOneShotApp : Application() {
    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(Timber.DebugTree())
//            Timber.plant(ReleaseTree())
        }
        fetchFcmToken()
        initAdmob()
    }


    fun fetchFcmToken() {
        CoroutineScope(Dispatchers.IO).launch {
            dataStoreRepository.getUserInfo() ?: return@launch
            val originFcmToken = dataStoreRepository.getPreferData(DataStoreKey.FcmToken)
            Timber.d("CheckFcmToken originFcmToken => ${originFcmToken}")
            val fcmToken = FcmExtension.getFcmToken() ?: return@launch
            Timber.d("CheckFcmToken fcmToken => ${fcmToken}")
            if (originFcmToken != fcmToken)
                dataStoreRepository.setPreferData(DataStoreKey.FcmToken, fcmToken)
        }
    }

    fun initAdmob(){
        CoroutineScope(Dispatchers.IO).launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@OneDayOneShotApp) {}
        }
    }


}


class ReleaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.ERROR || priority == Log.WARN) {
            Log.println(priority, tag, message)
        }
    }
}