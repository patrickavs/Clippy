package ir.amirroid.clipshare.history

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import ir.amirroid.clipshare.design_system.components.AppText
import ir.amirroid.clipshare.design_system.components.AppTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClipboardHistoryScreen() {
    Column {
        AppTopAppBar(
            title = {
                AppText("History")
            }
        )
    }
}