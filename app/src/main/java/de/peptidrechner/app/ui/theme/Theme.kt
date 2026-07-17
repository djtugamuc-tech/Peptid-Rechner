package de.peptidrechner.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightScheme = lightColorScheme(
    primary = Brand.Primary,
    onPrimary = Color.White,
    primaryContainer = Brand.PrimaryLight,
    onPrimaryContainer = Color.White,
    secondary = Brand.Accent,
    onSecondary = Color.White,
    secondaryContainer = Brand.AccentLight,
    onSecondaryContainer = Color.White,
    tertiary = Brand.Cyan,
    background = LightAppColors.bg,
    onBackground = LightAppColors.textStrong,
    surface = LightAppColors.cardBg,
    onSurface = LightAppColors.textStrong,
    surfaceVariant = LightAppColors.cardBgAlt,
    onSurfaceVariant = LightAppColors.textLight,
    outline = LightAppColors.border,
    outlineVariant = LightAppColors.borderLight,
    error = Brand.Danger,
    onError = Color.White,
)

private val DarkScheme = darkColorScheme(
    primary = Brand.PrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Brand.Primary,
    onPrimaryContainer = Color.White,
    secondary = Brand.Accent,
    onSecondary = Color.White,
    secondaryContainer = Brand.Accent,
    onSecondaryContainer = Color.White,
    tertiary = Brand.Cyan,
    background = DarkAppColors.bg,
    onBackground = DarkAppColors.textStrong,
    surface = DarkAppColors.cardBg,
    onSurface = DarkAppColors.textStrong,
    surfaceVariant = DarkAppColors.cardBgAlt,
    onSurfaceVariant = DarkAppColors.textLight,
    outline = DarkAppColors.border,
    outlineVariant = DarkAppColors.borderLight,
    error = Brand.Danger,
    onError = Color.White,
)

@Composable
fun PeptidRechnerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val appColors = if (darkTheme) DarkAppColors else LightAppColors
    val colorScheme = if (darkTheme) DarkScheme else LightScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Der Hero-Bereich oben ist in beiden Modi dunkel -> helle Statusleisten-Icons.
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content,
        )
    }
}
