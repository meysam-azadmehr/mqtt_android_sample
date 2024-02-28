package com.azadmehr.meysam.mqtt_client_test

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext applicationContext: Context,
) : ViewModel() {

    //  init ui state
    private val _state = MutableStateFlow(MainUIState())
    val state = _state.asStateFlow()

    private lateinit var mqttAndroidClient: MqttClient

    companion object {
        val LampStatusTopic: String = "/home/sensors/temp/kitchen"
        val LuxStatus: String = "/home/sensors/lux/kitchen"
    }

    init {

        viewModelScope.launch(Dispatchers.IO) {
            // create mqtt client
            mqttAndroidClient = MqttClient(context = applicationContext)
            triggerCallBack()
        }
    }

    private suspend fun triggerCallBack() {
        mqttAndroidClient.messageCallBack.collect { m ->
            _state.update {
                it.copy(message = m.message, topic = m.topic)
            }
            if (m.topic == LuxStatus) {
                if (m.message.toFloat() > 20) {
                    publishMessage(LampStatusTopic, "false")
                } else {
                    publishMessage(LampStatusTopic, "true")
                }
                _state.update {
                    it.copy(lux = m.message.toFloat())
                }
            } else if (m.topic == LampStatusTopic) {
                _state.update {
                    it.copy(lampStatus = m.message.toBoolean())
                }
            }
        }
    }

    fun publishMessage(topic: String, message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            mqttAndroidClient.publishMessage(
                message,
                topic,
            )
        }
    }

    fun subscribeOnTopic(topic: String) {
        viewModelScope.launch(Dispatchers.IO) {
            mqttAndroidClient.subscribeToTopic(topic)
            triggerCallBack()
        }
    }

    fun unSubscrbeAll() {
        viewModelScope.launch(Dispatchers.IO) {
            mqttAndroidClient.unSubscribeTopic(LampStatusTopic)
            mqttAndroidClient.unSubscribeTopic(LuxStatus)
            triggerCallBack()
        }
    }

    fun subscribeAll() {
        viewModelScope.launch(Dispatchers.IO) {
            mqttAndroidClient.subscribeToTopic(LampStatusTopic)
            mqttAndroidClient.subscribeToTopic(LuxStatus)
            triggerCallBack()
        }
    }
}