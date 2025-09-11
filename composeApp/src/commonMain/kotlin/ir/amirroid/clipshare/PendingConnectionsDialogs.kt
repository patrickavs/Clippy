package ir.amirroid.clipshare

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.amirroid.clipshare.design_system.components.AppButton
import ir.amirroid.clipshare.design_system.components.AppText

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