package com.azadmehr.meysam.mqtt_client_test

import android.content.Context
import android.util.Log
import info.mqtt.android.service.MqttAndroidClient
import info.mqtt.android.service.QoS
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage

class MqttClient(private var context: Context) {
    var connectStatus: Boolean = false
    val mqttClient: MqttAndroidClient

    data class MessageArrived(val message: String, val topic: String)

    init {
        mqttClient = getInstance(context)
        connect()
    }

    var messageCallBack = callbackFlow<MessageArrived> {
        val callback = object : MqttCallbackExtended {

            override fun connectionLost(cause: Throwable?) {
                Log.e("TAG", "connectionLost: ${cause?.message}")
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.e("TAG", "messageArrived: ${message.toString()} on topic : $topic")
                trySend(MessageArrived(message = message.toString(), topic = topic.toString()))
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.e("TAG", "deliveryComplete: ${token.toString()}")
            }

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                Log.e("TAG", "connectComplete")
            }
        }

        mqttClient.addCallback(callback)
        awaitClose {
            disconnect()
            mqttClient.unregisterResources()
        }

    }

    companion object {
        const val brokerUrl = "tcp://192.168.60.210:1883"
        var clientId = "te"
    }


    private fun getInstance(context: Context): MqttAndroidClient {
        return MqttAndroidClient(context, brokerUrl, clientId)
    }

    fun publishMessage(publishMessage: String, topic: String) {
        Log.e("TAG", "publishMessage: $publishMessage")
        val message = MqttMessage()
        message.payload = publishMessage.toByteArray()
        if (mqttClient.isConnected) {
            mqttClient.publish(topic, message)
            if (!mqttClient.isConnected) {
//                addToHistory(mqttAndroidClient.bufferedMessageCount.toString() + " messages in buffer.")
            }
        } else {
            Log.e("TAG", "Not connect")
        }
    }

    fun subscribeToTopic(subscriptionTopic: String) {
//        mqttClient.subscribe(subscriptionTopic, QoS.AtMostOnce.value, null, object :
//            IMqttActionListener {
//            override fun onSuccess(asyncActionToken: IMqttToken) {
//                Log.e("TAG", "onSuccess:$subscriptionTopic")
//            }
//
//            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
//                Log.e("TAG", "onFailure:$exception")
//            }
//        })

        // THIS DOES NOT WORK!
        if (connectStatus) {
            mqttClient.subscribe(subscriptionTopic, QoS.AtMostOnce.value) { topic, message ->
                Log.e("TAG", "subscribe on topic $topic")
            }
        }
    }

    fun unSubscribeTopic(topic: String) {
        Log.e("TAG", "unsubscribe on topic $topic")
        mqttClient.unsubscribe(topic)
    }

    fun connect() {
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.userName = "admin"
        mqttConnectOptions.password = "123".toCharArray()
        mqttConnectOptions.isAutomaticReconnect = true
        mqttConnectOptions.isCleanSession = false
        Log.e("TAG", "Connecting: $brokerUrl")
        mqttClient.connect(mqttConnectOptions, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                val disconnectedBufferOptions = DisconnectedBufferOptions()
                disconnectedBufferOptions.isBufferEnabled = true
                disconnectedBufferOptions.bufferSize = 100
                disconnectedBufferOptions.isPersistBuffer = false
                disconnectedBufferOptions.isDeleteOldestMessages = false

                Log.e("TAG", "connected to : $brokerUrl")
                mqttClient.setBufferOpts(disconnectedBufferOptions)
                connectStatus = true
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.e("TAG", "onFailure: Fail to connect ${exception?.message.toString()}")
            }
        })
    }

    fun disconnect() {
        mqttClient.disconnect()
    }

}