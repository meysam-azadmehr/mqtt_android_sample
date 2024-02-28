package com.azadmehr.meysam.mqtt_client_test

data class MainUIState(
    var message: String = "",
    var lampStatus: Boolean = false,
    var lux: Float = 0.0f,
    var topic: String = "",
)
