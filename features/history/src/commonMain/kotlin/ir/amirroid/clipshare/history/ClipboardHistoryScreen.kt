package ir.amirroid.clipshare.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.amirroid.clipshare.design_system.components.AppCard
import ir.amirroid.clipshare.design_system.components.AppText
import ir.amirroid.clipshare.design_system.components.AppTopAppBar
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClipboardHistoryScreen(
    viewModel: ClipboardHistoryViewModel = koinViewModel()
) {
    val history by viewModel.history.collectAsStateWithLifecycle()

    Column {
        AppTopAppBar(
            title = {
                AppText("History")
            }
        )
        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(history, key = { it.id }) {
                AppCard {
                    Text(it.toString(), modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}