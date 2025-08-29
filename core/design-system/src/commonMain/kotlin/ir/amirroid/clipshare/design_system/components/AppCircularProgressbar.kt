package ir.amirroid.clipshare.design_system.components

import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppCircularProgressbar(modifier: Modifier = Modifier) {
    CircularWavyProgressIndicator(
        modifier = modifier
    )
}