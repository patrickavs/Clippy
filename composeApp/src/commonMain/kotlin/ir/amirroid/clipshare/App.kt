package ir.amirroid.clipshare

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import ir.amirroid.clipshare.design_system.components.AppSurface
import ir.amirroid.clipshare.design_system.theme.ClipShareTheme
import ir.amirroid.clipshare.navigation.MainNavigation

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun App() {
    ClipShareTheme {
        AppSurface {
            MainNavigation()
        }
    }
}