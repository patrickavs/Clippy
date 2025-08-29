package ir.amirroid.clipshare

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.amirroid.clipshare.design_system.components.AppCard
import ir.amirroid.clipshare.design_system.components.AppCircularProgressbar
import ir.amirroid.clipshare.design_system.components.AppSurface
import ir.amirroid.clipshare.design_system.theme.ClipShareTheme
import ir.amirroid.clipshare.navigation.MainNavigation

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun App() {
    ClipShareTheme {
        AppSurface {
            MainNavigation()
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                AppCard {
                    AppCircularProgressbar(modifier = Modifier.padding(24.dp))
                }
            }
        }
    }
}