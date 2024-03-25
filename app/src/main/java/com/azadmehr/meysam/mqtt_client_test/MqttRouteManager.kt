package com.azadmehr.meysam.mqtt_client_test

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class MqttRouteManager(
    private val ioDispatcher: CoroutineDispatcher,
) {

    public suspend fun getIp(ip: String) {
        withContext(ioDispatcher) {
            Log.e("TAG", "getIp: $ip")
        }
    }

    public suspend fun getMac(mac : String) {
        withContext(ioDispatcher){
            Log.e("TAG", "getMac: $mac")
        }
    }
}