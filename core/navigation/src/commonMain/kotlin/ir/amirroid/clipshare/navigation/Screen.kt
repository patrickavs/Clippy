package ir.amirroid.clipshare.navigation

import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.History
import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable

data class NavigationBarItem(
    val name: String,
    val screen: Screen,
    val icon: @Composable () -> Unit
)

sealed interface Screen {
    @Serializable
    data object History : Screen

    @Serializable
    data object Devices : Screen

    @Serializable
    data object QrCode : Screen

    @Serializable
    data object Scanner : Screen

    companion object {
        val navigationPages = listOf(
            NavigationBarItem(
                name = "History",
                screen = History,
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.History,
                        contentDescription = null
                    )
                }
            ),
            NavigationBarItem(
                name = "Devices",
                screen = Devices,
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Devices,
                        contentDescription = null
                    )
                }
            ),
        )
    }
}