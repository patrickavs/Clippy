package ir.amirroid.clipshare

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import ir.amirroid.clipshare.common.app.events.EventBus
import ir.amirroid.clipshare.common.app.models.NotificationRequest
import ir.amirroid.clipshare.design_system.theme.ClipShareTheme
import ir.amirroid.clipshare.navigation.MainNavigation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun App() {
    val snakeState = rememberSnackbarHostState()

    ClipShareTheme {
        Scaffold(
            snackbarHost = {
                SnackbarHost(snakeState) {
                    Snackbar(it)
                }
            }
        ) {
            MainNavigation()
        }
    }
}

@Composable
private fun rememberSnackbarHostState(): SnackbarHostState {
    val snakeState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        EventBus.subscribe<NotificationRequest> {
            launch {
                snakeState.showSnackbar(
                    message = it.title,
                    actionLabel = it.description
                )
            }
        }
    }
    return snakeState
}