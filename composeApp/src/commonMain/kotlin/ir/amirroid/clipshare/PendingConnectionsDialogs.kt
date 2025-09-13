package ir.amirroid.clipshare

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DesktopWindows
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.PhoneIphone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.amirroid.clipshare.design_system.components.AppButton
import ir.amirroid.clipshare.design_system.components.AppSurface
import ir.amirroid.clipshare.design_system.components.AppText
import ir.amirroid.clipshare.domain.models.utils.DevicePlatform

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PendingConnectionsDialogs(
    viewModel: MainViewModel
) {
    val pendingConnections by viewModel.pendingConnections.collectAsStateWithLifecycle()

    pendingConnections.forEach { connection ->
        AlertDialog(
            onDismissRequest = { },
            title = {
                AppText("Connection Request")
            },
            text = {
                Column {
                    AppText("Device Name: ${connection.name}")
                    AppText("Platform: ${connection.platform}")
                    Spacer(modifier = Modifier.height(8.dp))
                    AppText(
                        "This device wants to connect with you. " +
                                "If you accept, future connections from this device will not require approval.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            icon = {
                AppSurface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialShapes.Sunny.toShape(),
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        PlatformIcon(connection.platform)
                    }
                }
            },
            confirmButton = {
                AppButton(onClick = {
                    viewModel.accept(connection.id)
                }) {
                    AppText("Accept")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.reject(connection.id)
                }) {
                    AppText("Reject")
                }
            }
        )
    }
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