package ir.amirroid.clipshare.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ir.amirroid.clipshare.history.ClipboardHistoryScreen

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.History
    ) {
        composable<Screen.History> {
            ClipboardHistoryScreen()
        }
    }
}