package ir.amirroid.clipshare

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import coil3.compose.AsyncImage
import ir.amirroid.clipshare.clipboard.manager.ClipboardManager
import ir.amirroid.clipshare.clipboard.models.ClipboardContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun App() {
    val entries = remember {
        mutableStateListOf<ClipboardContent>()
    }
    val scope = rememberCoroutineScope()
    val manager: ClipboardManager = koinInject()
    DisposableEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            manager.addOnChangedListener {
                entries.add(it)
            }
        }
        onDispose { }
    }

    LazyColumn {
        item {
            SelectionContainer {
                Text("Test")
            }
        }
        items(entries) {
            if (it is ClipboardContent.Image) {
                AsyncImage(
                    model = it.bytes,
                    contentDescription = null
                )
            } else {
                ListItem(headlineContent = {
                    Text(it.toString())
                })
            }
        }
    }
}