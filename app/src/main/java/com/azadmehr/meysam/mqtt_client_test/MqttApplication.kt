package com.azadmehr.meysam.mqtt_client_test

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MqttApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}