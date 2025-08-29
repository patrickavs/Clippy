package ir.amirroid.clipshare.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object History : Screen
}