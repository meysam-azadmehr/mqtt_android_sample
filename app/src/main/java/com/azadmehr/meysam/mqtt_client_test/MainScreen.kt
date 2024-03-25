package com.azadmehr.meysam.mqtt_client_test

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {

    val state = viewModel.state.collectAsState()

    Column(modifier = Modifier.padding(8.dp)) {
        Button(onClick = {
           viewModel.unSubscribeAll()
        }) {
           Text(text = "Unsubscribe All")
        }
        Button(onClick = {
            viewModel.subscribeAll()
        }) {
            Text(text = "Subscribe All")
        }
        Text(text = "message : ${state.value.message}")
        Text(text = "topic : ${state.value.topic}")
        LazyRow() {
            item {
                LampItem(
                    onChangeStatus = { status ->
                        viewModel.publishMessage(
                            topic = "/home/room/sensors/lux",
                            message = status.toString()
                        )
                    },
                    status = state.value.lampStatus
                ) {

                }
                SwitchItem(
                    onChangeStatus = { status ->
                        viewModel.publishMessage(
                            topic = "/home/room/sensors/lux",
                            message = status.toString()
                        )
                    },
                    status = state.value.lampStatus
                ) {

                }

                SensorLuxItem(lux = state.value.lux)
            }
        }

    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SwitchItem(
    modifier: Modifier = Modifier,
    onChangeStatus: (status: Boolean) -> Unit,
    status: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(140.dp)
            .padding(4.dp),
        onClick = { onClick() }
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            Icon(
                imageVector = Icons.Outlined.PlayArrow,
                contentDescription = "",
                modifier = Modifier
                    .size(60.dp)
                    .padding(6.dp)
            )
            Text(
                text = "Switch",
                style = MaterialTheme.typography.labelMedium.copy(textAlign = TextAlign.Center),
                modifier = Modifier.basicMarquee()
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = status,
                    onCheckedChange = {
                        onChangeStatus(it)
                    },
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LampItem(
    modifier: Modifier = Modifier,
    onChangeStatus: (status: Boolean) -> Unit,
    status: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(140.dp)
            .padding(4.dp),
        onClick = { onClick() }
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            Icon(
                imageVector = Icons.Outlined.Lightbulb,
                contentDescription = "",
                modifier = Modifier
                    .size(60.dp)
                    .padding(6.dp)
            )
            Text(
                text = "Lamp",
                style = MaterialTheme.typography.labelMedium.copy(textAlign = TextAlign.Center),
                modifier = Modifier.basicMarquee()
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = status,
                    onCheckedChange = {
                        onChangeStatus(it)
                    },
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SensorLuxItem(
    modifier: Modifier = Modifier,
    lux: Float,
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(140.dp)
            .padding(4.dp),
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            Icon(
                imageVector = Icons.Outlined.Sensors,
                contentDescription = "",
                modifier = Modifier
                    .size(60.dp)
                    .padding(6.dp)
            )
            Text(
                text = "Sensor Lux",
                style = MaterialTheme.typography.labelMedium.copy(textAlign = TextAlign.Center),
                modifier = Modifier.basicMarquee()
            )

            Text(
                text = lux.toString(),
                style = MaterialTheme.typography.labelMedium.copy(textAlign = TextAlign.Center),
                modifier = Modifier.basicMarquee()
            )

            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}
