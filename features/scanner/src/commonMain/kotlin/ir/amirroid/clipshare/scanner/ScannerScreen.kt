package ir.amirroid.clipshare.scanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.DesktopWindows
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.PhoneIphone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import ir.amirroid.clipshare.design_system.components.AppButton
import ir.amirroid.clipshare.design_system.components.AppCard
import ir.amirroid.clipshare.design_system.components.AppIconButton
import ir.amirroid.clipshare.design_system.components.AppSurface
import ir.amirroid.clipshare.design_system.components.AppText
import ir.amirroid.clipshare.design_system.components.AppTopAppBar
import ir.amirroid.clipshare.domain.models.utils.DevicePlatform
import ir.amirroid.clipshare.ui_models.device.DeviceUiModel
import org.koin.compose.viewmodel.koinViewModel
import org.publicvalue.multiplatform.qrcode.CodeType
import org.publicvalue.multiplatform.qrcode.ScannerWithPermissions

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ScannerScreen(
    onBack: () -> Unit,
    viewModel: ScannerViewModel = koinViewModel()
) {
    val scrollState = rememberScrollState()
    Column {
        AppTopAppBar(
            title = {
                AppText("Scanner")
            },
            navigationIcon = {
                AppIconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBackIos,
                        contentDescription = null
                    )
                }
            }
        )
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val barcodeSize = minOf(minOf(maxWidth, maxHeight) * .8f, 350.dp)
            Column(
                modifier = Modifier.heightIn(min = maxHeight).verticalScroll(scrollState)
                    .padding(vertical = 24.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppCard(modifier = Modifier.size(barcodeSize)) {
                    ScannerWithPermissions(
                        modifier = Modifier.padding(24.dp).clip(MaterialTheme.shapes.small)
                            .fillMaxSize(),
                        onScanned = {
                            viewModel.parseDevice(it)
                            false
                        },
                        types = listOf(CodeType.QR)
                    )
                }
                AppCard(modifier = Modifier.padding(top = 16.dp).width(barcodeSize)) {
                    AppText(
                        text = "Point your device’s camera at the other device’s QR code and hold it steady until it is scanned successfully.",
                        modifier = Modifier.padding(24.dp).fillMaxWidth().alpha(.7f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }

    viewModel.foundDevice?.let { foundDevice ->
        DeviceConnectionDialog(
            foundDevice = foundDevice,
            onConnect = {
                viewModel.connectToDevice(foundDevice)
                viewModel.foundDevice = null
                onBack.invoke()
            },
            onDismiss = { viewModel.foundDevice = null }
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DeviceConnectionDialog(
    foundDevice: DeviceUiModel,
    onConnect: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            AppText("Connect to Device")
        },
        text = {
            Column {
                val annotatedText = buildAnnotatedString {
                    append("You have scanned the QR code of device: ")
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        append(foundDevice.name)
                    }
                    append(" (${foundDevice.platform}).\n\nDo you want to connect to this device?")
                }

                AppText(
                    text = annotatedText,
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
                    PlatformIcon(foundDevice.platform)
                }
            }
        },
        confirmButton = {
            AppButton(onClick = onConnect) {
                AppText("Connect")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                AppText("Cancel")
            }
        }
    )
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