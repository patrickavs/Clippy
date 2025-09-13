package ir.amirroid.clipshare.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ir.amirroid.clipshare.design_system.components.AppText
import ir.amirroid.clipshare.devices.DevicesScreen
import ir.amirroid.clipshare.history.ClipboardHistoryScreen
import ir.amirroid.clipshare.qrcode.QrCodeScreen
import ir.amirroid.clipshare.scanner.ScannerScreen

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    AdaptiveNavigationLayout(navController, onNavigate = { screen ->
        navController.navigate(screen) {
            navController.currentDestination?.let {
                popUpTo(it.id) {
                    inclusive = true
                    saveState = true
                }
            }
            launchSingleTop = true
            restoreState = true
        }
    }) {
        NavHost(
            navController = navController,
            startDestination = Screen.History,
            modifier = Modifier.fillMaxSize()
        ) {
            composable<Screen.History> {
                ClipboardHistoryScreen()
            }
            composable<Screen.Devices> {
                DevicesScreen(
                    onGoToScanner = { navController.navigate(Screen.Scanner) },
                    onGoToQrCode = { navController.navigate(Screen.QrCode) }
                )
            }
            composable<Screen.QrCode> {
                QrCodeScreen(
                    onBack = navController::navigateUp
                )
            }
            composable<Screen.Scanner> {
                ScannerScreen(
                    onBack = navController::navigateUp
                )
            }
        }
    }
}

@Composable
private fun AdaptiveNavigationLayout(
    navController: NavHostController,
    onNavigate: (Screen) -> Unit,
    content: @Composable () -> Unit
) {
    val pages = Screen.navigationPages
    val windowSize = LocalWindowInfo.current
    val density = LocalDensity.current
    val widthDp = with(density) { windowSize.containerSize.width.toDp().value }

    val isCompact = widthDp < 600
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomStart) {
        Box(
            Modifier.then(
                if (isCompact) Modifier.padding(bottom = 80.dp)
                    .navigationBarsPadding() else Modifier.padding(start = 80.dp)
            )
        ) { content() }
        if (isCompact) {
            NavigationBar {
                pages.forEach { page ->
                    val selected = remember(currentDestination) {
                        currentDestination?.hasRoute(page.screen::class) == true
                    }
                    NavigationBarItem(
                        selected = selected,
                        onClick = { onNavigate.invoke(page.screen) },
                        icon = page.icon,
                        label = { AppText(page.name) }
                    )
                }
            }
        } else {
            NavigationRail(
                windowInsets = WindowInsets(top = 12.dp)
            ) {
                pages.forEach { page ->
                    val selected = remember(currentDestination) {
                        currentDestination?.hasRoute(page.screen::class) == true
                    }
                    NavigationRailItem(
                        selected = selected,
                        onClick = { onNavigate.invoke(page.screen) },
                        icon = page.icon,
                        label = { AppText(page.name) }
                    )
                }
            }
        }
    }
}
