package com.azadmehr.meysam.mqtt_client_test

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext applicationContext: Context,
) : ViewModel() {

    private val mqttRouteManager: MqttRouteManager = MqttRouteManager(Dispatchers.IO)

    //  init ui state
    private val _state = MutableStateFlow(MainUIState())
    val state = _state.asStateFlow()

    private lateinit var mqttAndroidClient: MqttClient

    companion object {
        // todo : for best experience must set unit id for mqtt client in dashboard
        //  set id of unit in device
        val setUnitRoute: String = "/req/set_unit" // todo : fix route  (set unit id)

        //  set id of place  in device
        val setPlaceRoute: String = "/req/set_place" // todo : fix route (set unit id)

        //  get ip of device route
        val getIpRoute: String = "/#/#/#/#/get_ip" // todo  : fix route (set unit id)

        //  get mac of device route
        val getMacRoute: String = "/#/#/#/#/get_mac"

        //  get strength of wifi
        val getWifiStrengthRoute: String = "/#/#/#/#/wifi_strength" // todo: fix route (set unit id)

        //  get esp temperature
        val getEspTemperatureRoute: String =
            "/#/#/#/#/get_esp_temperature" // todo:  fix route (set unit id)

        //  disable device
        val disableNodeRoute: String = "/#/#/#/#/disable" // todo: fix route (set unit id)

        //  enable device
        val enableRoute: String = "/#/#/#/#/enable" // todo : fix route (set unit id)

        // notify device ready
        val readyDeviceRoute: String = "/#/#/#/res/ready" // todo: fix route (set unit id)

        //  set new wifi for device
        val sendNewWifiRoute = "/#/#/#/req/send_new_wifi" // todo : fix route  (set unit id)

        //  response of send new wifi
        val setNewWifiRoute = "/#/#/#/res/set_new_wifi" // todo : fix route  (set unit id)

        // go to new wifi network
        val goToNewWifiRoute = "/#/#/#/req/goto_new_wifi" // todo : fix route (set unit id)

        //  save energy mode
        val saveEnergyRoute = "#/#/#/#/save_energy" //  todo  : fix route (set unit id)

        // lock device
        val lockRoute = "#/#/#/#/lock" // todo :   fix route (set unit id)

        // info  device
        val infoRoute = "/#/#/#/#/info" // todo : fix route (set unit id)

        // ping
        val pingRoute = "#/#/#/#/ping"  // todo:fix route (set unit id)

        //pong
        val pongRoute = "#/#/#/#/pong" // todo:fix route (set unit id)

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
            if (m.topic.contains(regex = Regex("/1$"))) {
                _state.update {
                    it.copy(lampStatus = JSONObject(m.message).getBoolean("status"))
                }
            }
            if (m.topic.contains(regex = Regex("/3$"))) {
                _state.update {
                    it.copy(switchStatus = JSONObject(m.message).getBoolean("status"))
                }
            }
            if (m.topic.contains(regex = Regex("/2$"))) {
                Log.e("TAG", "triggerCallBack: ${m.message}", )
                _state.update {
                    it.copy(lux = JSONObject(m.message).getInt("lux").toFloat())
                }
            }
            if (m.topic.contains("/res/get_ip")) {
                mqttRouteManager.getIp(m.message)
            }
            if (m.topic.contains("/res/get_mac")) {
                mqttRouteManager.getMac(m.message)
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

    fun unSubscribeAll() {
        viewModelScope.launch(Dispatchers.IO) {
            mqttAndroidClient.unSubscribeTopic("/home/unit/place/#")
            triggerCallBack()
        }
    }

    fun subscribeAll() {
        viewModelScope.launch(Dispatchers.IO) {
            mqttAndroidClient.subscribeToTopic("/home/unit/place/#")
            triggerCallBack()
        }
    }

    fun getIp(deviceId: String) {
        viewModelScope.launch {
            mqttAndroidClient.publishMessage("", topic = "/#/#/$deviceId/req/get_ip")
        }
    }
}