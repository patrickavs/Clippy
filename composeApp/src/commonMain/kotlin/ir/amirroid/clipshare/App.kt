package ir.amirroid.clipshare

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ir.amirroid.clipshare.connectivity.broadcast.DeviceBroadcastService
import ir.amirroid.clipshare.connectivity.discovery.DeviceDiscoveryService
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun App() {
    val service: DeviceDiscoveryService = koinInject()
    val broadcastService: DeviceBroadcastService = koinInject()
    val isStarted by service.isStarted.collectAsState()
    val scope = rememberCoroutineScope()
    val incoming by service.incoming.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            scope.launch {
                broadcastService.stopBroadcasting()
            }
        }
    }

    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = {
                scope.launch {
                    broadcastService.startBroadcasting()
                }
            }) {
                Text("Send")
            }
            Button(onClick = {
                scope.launch {
                    if (isStarted) {
                        service.stopDiscovery()
                    } else {
                        service.startDiscovery()
                    }
                }
            }) {
                Text("Discovery")
            }
            Column {
                incoming.forEach {
                    ListItem(headlineContent = {
                        Text(it.name)
                    }, supportingContent = {
                        Text(it.ip)
                    }, overlineContent = {
                        Text(it.deviceId)
                    })
                }
            }
        }
    }
}