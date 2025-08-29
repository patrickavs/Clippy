package ir.amirroid.clipshare.design_system.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF0055FF),
    onPrimary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color(0xFFF5F5F5),
    onSurface = Color(0xFF111111),
    outline = Color(0xFF888888),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF4D8DFF),
    onPrimary = Color.Black,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color(0xFF1A1A1A),
    onSurface = Color(0xFFEAEAEA),
    outline = Color(0xFF666666),
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ClipShareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        content = content
    )
}