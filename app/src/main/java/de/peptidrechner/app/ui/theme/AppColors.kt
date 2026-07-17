package de.peptidrechner.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Theme-abhängige, neutrale Farben (Hintergrund, Karten, Text, Rahmen).
 * Die Markenfarben (Lila/Pink) bleiben in [Brand] und sind in beiden Modi gleich.
 */
data class AppColors(
    val bg: Color,
    val cardBg: Color,
    val cardBgAlt: Color,
    val textStrong: Color,
    val textLight: Color,
    val textMuted: Color,
    val border: Color,
    val borderLight: Color,
)

// Light: bewusst dunkle, kräftige Schrift (fast schwarz) für gute Lesbarkeit.
val LightAppColors = AppColors(
    bg = Color(0xFFF8F9FC),
    cardBg = Color(0xFFFFFFFF),
    cardBgAlt = Color(0xFFEDEEF5),
    textStrong = Color(0xFF10121A),
    textLight = Color(0xFF2A2F3D),
    textMuted = Color(0xFF515971),
    border = Color(0xFFE1E4EE),
    borderLight = Color(0xFFEDEEF5),
)

val DarkAppColors = AppColors(
    bg = Color(0xFF0E0F16),
    cardBg = Color(0xFF1A1D27),
    cardBgAlt = Color(0xFF262A38),
    textStrong = Color(0xFFF4F5FA),
    textLight = Color(0xFFC9CEDC),
    textMuted = Color(0xFF8D94A8),
    border = Color(0xFF2C313F),
    borderLight = Color(0xFF242936),
)

val LocalAppColors = staticCompositionLocalOf { LightAppColors }

/** Ergonomischer Zugriff auf die aktiven neutralen Farben in Composables. */
object AppC {
    val bg: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.bg
    val cardBg: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.cardBg
    val cardBgAlt: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.cardBgAlt
    val textStrong: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.textStrong
    val textLight: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.textLight
    val textMuted: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.textMuted
    val border: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.border
    val borderLight: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.borderLight
}
