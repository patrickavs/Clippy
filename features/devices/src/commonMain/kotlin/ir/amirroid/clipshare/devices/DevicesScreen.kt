package ir.amirroid.clipshare.devices

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DesktopWindows
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.PhoneIphone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.amirroid.clipshare.design_system.components.AppButton
import ir.amirroid.clipshare.design_system.components.AppListItem
import ir.amirroid.clipshare.design_system.components.AppText
import ir.amirroid.clipshare.design_system.components.AppTopAppBar
import ir.amirroid.clipshare.design_system.components.ExpandableSection
import ir.amirroid.clipshare.domain.models.utils.DevicePlatform
import ir.amirroid.clipshare.ui_models.connected_device.ConnectedDeviceUiModel
import ir.amirroid.clipshare.ui_models.device.DeviceUiModel
import kotlinx.collections.immutable.ImmutableList
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DevicesScreen(
    viewModel: DevicesViewModel = koinViewModel()
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val columnsCount = rememberColumnCount()

    DisposableEffect(Unit) {
        viewModel.startDiscovering()
        viewModel.startBroadcasting()
        onDispose {
            viewModel.stopDiscovering()
            viewModel.startBroadcasting()
        }
    }

    Column {
        AppTopAppBar(
            title = {
                AppText("Devices")
            }
        )
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(columnsCount),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalItemSpacing = 12.dp,
            contentPadding = PaddingValues(16.dp)
        ) {
            item("devices") {
                ExpandableSection(
                    title = "Connected Devices",
                    modifier = Modifier.weight(1f).animateItem(),
                    expandedByDefault = true
                ) {
                    ConnectedDevicesList(screenState.connectedDevices)
                }
            }
            item("nearby_devices") {
                ExpandableSection(
                    title = "Nearby Devices",
                    icons = { LoadingIndicator(modifier = Modifier.size(40.dp)) },
                    modifier = Modifier.weight(1f).animateItem(),
                    expandedByDefault = true
                ) {
                    DevicesList(screenState.nearbyDevices, onConnect = viewModel::connectToDevice)
                }
            }
            item("broadcast_devices") {
                ExpandableSection(
                    title = "Make My Device Discoverable",
                    modifier = Modifier.weight(1f).animateItem(),
                    expandedByDefault = true
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        BroadcastStatus(
                            isBroadcasting = screenState.isBroadcasting,
                            onStop = viewModel::stopBroadcasting,
                            onStart = viewModel::startBroadcasting
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BroadcastStatus(
    isBroadcasting: Boolean,
    onStart: () -> Unit = {},
    onStop: () -> Unit = {}
) {
    val descriptionText =
        "When broadcasting is enabled, your device will be discoverable by others on the local network. " +
                "This allows nearby devices to connect and share data with you. Make sure you trust the network before starting. " +
                "Note: Broadcasting will automatically stop if you leave this screen."

    AnimatedContent(targetState = isBroadcasting) { broadcasting ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (broadcasting) {
                LoadingIndicator(modifier = Modifier.size(64.dp))

                AppText(
                    "Broadcasting My Device...",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                AppButton(onClick = onStop) {
                    AppText("Stop")
                }

                Spacer(modifier = Modifier.height(12.dp))

                AppText(
                    descriptionText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

            } else {
                AppButton(onClick = onStart) {
                    AppText("Start Broadcasting")
                }

                Spacer(modifier = Modifier.height(12.dp))

                AppText(
                    descriptionText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun DevicesList(
    devices: ImmutableList<DeviceUiModel>,
    onConnect: (DeviceUiModel) -> Unit
) {
    Column(modifier = Modifier.animateContentSize().fillMaxWidth()) {
        devices.forEach { device ->
            key(device.id) {
                DeviceItem(device, onConnect = { onConnect.invoke(device) })
            }
        }
    }
}

@Composable
fun DeviceItem(device: DeviceUiModel, onConnect: () -> Unit) {
    AppListItem(headlineContent = {
        AppText(device.name)
    }, leadingContent = {
        PlatformIcon(device.platform)
    }, trailingContent = {
        AppButton(onClick = onConnect) {
            Text("Connect")
        }
    })
}

@Composable
private fun ConnectedDevicesList(
    devices: ImmutableList<ConnectedDeviceUiModel>,
) {
    Column(modifier = Modifier.animateContentSize().fillMaxWidth()) {
        devices.forEach { connectedDevice ->
            key(connectedDevice.device.id) {
                ConnectedDeviceItem(connectedDevice)
            }
        }
    }
}

@Composable
fun ConnectedDeviceItem(connectedDevice: ConnectedDeviceUiModel) {
    AppListItem(headlineContent = {
        AppText(connectedDevice.device.name)
    }, leadingContent = {
        PlatformIcon(connectedDevice.device.platform)
    }, trailingContent = {
        Text(connectedDevice.connectionStatus.name)
    })
}

@Composable
private fun PlatformIcon(platform: DevicePlatform) {
    when (platform) {
        DevicePlatform.ANDROID -> {
            Icon(
                Icons.Rounded.PhoneAndroid,
                contentDescription = null
            )
        }

        DevicePlatform.IOS -> {
            Icon(
                Icons.Rounded.PhoneIphone,
                contentDescription = null
            )
        }

        DevicePlatform.DESKTOP -> {
            Icon(
                Icons.Rounded.DesktopWindows,
                contentDescription = null
            )
        }
    }
}


@Composable
fun rememberColumnCount(): Int {
    val windowSize = LocalWindowInfo.current
    val density = LocalDensity.current
    val widthDp = with(density) { windowSize.containerSize.width.toDp().value }

    return calculateColumns(widthDp)
}

fun calculateColumns(widthDp: Float): Int = when {
    widthDp >= 800 -> 2
    else -> 1
}