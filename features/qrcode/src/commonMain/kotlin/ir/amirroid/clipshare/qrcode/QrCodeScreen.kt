package ir.amirroid.clipshare.qrcode

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.brush
import io.github.alexzhirkevich.qrose.options.circle
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.options.solid
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import ir.amirroid.clipshare.design_system.components.AppCard
import ir.amirroid.clipshare.design_system.components.AppIconButton
import ir.amirroid.clipshare.design_system.components.AppText
import ir.amirroid.clipshare.design_system.components.AppTopAppBar
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrCodeScreen(
    onBack: () -> Unit,
    viewModel: QrCodeViewModel = koinViewModel()
) {
    val scrollState = rememberScrollState()
    Column {
        AppTopAppBar(
            title = {
                AppText("QRCode")
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
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val barcodeSize = minOf(minOf(maxWidth, maxHeight) * .8f, 350.dp)
            Column(
                modifier = Modifier.heightIn(min = maxHeight).verticalScroll(scrollState)
                    .padding(vertical = 24.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppCard(modifier = Modifier.size(barcodeSize)) {
                    Image(
                        painter = rememberQrCodePainter(viewModel.deviceInfoJson) {
                            shapes {
                                ball = QrBallShape.circle()
                                darkPixel = QrPixelShape.roundCorners()
                                frame = QrFrameShape.roundCorners(.25f)
                            }
                            colors {
                                dark = QrBrush.brush {
                                    Brush.linearGradient(
                                        0f to Color.Red,
                                        1f to Color.Blue,
                                        end = Offset(it, it)
                                    )
                                }
                                frame = QrBrush.solid(Color.Black)
                            }
                        },
                        contentDescription = null,
                        modifier = Modifier.padding(24.dp).fillMaxSize(),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(LocalContentColor.current)
                    )
                }
                AppCard(modifier = Modifier.padding(top = 16.dp).width(barcodeSize)) {
                    AppText(
                        text = "Open Devices > Tap the scan icon > Point your camera at this QR code to connect your device.",
                        modifier = Modifier.padding(24.dp).fillMaxWidth().alpha(.7f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}