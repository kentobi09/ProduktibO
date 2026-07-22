package com.produktibo.launcher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val OledBlack = Color(0xFF000000)
val DarkSurface = Color(0xFF09090B)
val TextMain = Color(0xFFF4F4F5)
val TextMuted = Color(0xFF71717A)
val AccentBorder = Color(0xFF27272A)

private val DarkColorScheme = darkColorScheme(
    primary = TextMain,
    background = OledBlack,
    surface = DarkSurface,
    onPrimary = OledBlack,
    onBackground = TextMain,
    onSurface = TextMain
)

@Composable
fun ProduktibOTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
