package de.peptidrechner.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val BioColors = lightColorScheme(
    primary = Brand.Primary,
    onPrimary = Color.White,
    primaryContainer = Brand.PrimaryLight,
    onPrimaryContainer = Color.White,
    secondary = Brand.Accent,
    onSecondary = Color.White,
    secondaryContainer = Brand.AccentLight,
    onSecondaryContainer = Color.White,
    tertiary = Brand.Cyan,
    background = Brand.Bg,
    onBackground = Brand.TextStrong,
    surface = Brand.CardBg,
    onSurface = Brand.TextStrong,
    surfaceVariant = Brand.CardBgAlt,
    onSurfaceVariant = Brand.TextLight,
    outline = Brand.Border,
    outlineVariant = Brand.BorderLight,
    error = Brand.Danger,
    onError = Color.White,
)

@Composable
fun PeptidRechnerTheme(content: @Composable () -> Unit) {
    // Fester Marken-Look – kein dynamisches Material-You.
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Helle Statusbar-Icons NICHT (Hero ist dunkel) -> dunkle Icons auf hellem Rest.
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    MaterialTheme(
        colorScheme = BioColors,
        typography = AppTypography,
        content = content,
    )
}
