package com.echologic

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent

@HiltAndroidApp
class EchoLogicApp : Application(), Configuration.Provider {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface HiltWorkerFactoryEntryPoint {
        fun workerFactory(): HiltWorkerFactory
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        // Absolute first: Initialize Firebase before Hilt
        try {
            FirebaseApp.initializeApp(base)
        } catch (e: Exception) {
            // Silently catch if already initialized
        }
    }

    override fun onCreate() {
        super.onCreate()

        // side-effect SDKs wrapped in safety
        try {
            MobileAds.initialize(this) {}
        } catch (e: Exception) {
            android.util.Log.e("EchoLogicApp", "AdMob init failed")
        }
    }

    override val workManagerConfiguration: Configuration
        get() {
            val entryPoint = EntryPointAccessors.fromApplication(
                this,
                HiltWorkerFactoryEntryPoint::class.java
            )
            return Configuration.Builder()
                .setWorkerFactory(entryPoint.workerFactory())
                .build()
        }
}
