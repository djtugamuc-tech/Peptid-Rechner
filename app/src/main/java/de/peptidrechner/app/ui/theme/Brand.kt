package de.peptidrechner.app.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Design-Tokens der App:
 * Primär Lila #6C3AE0, Akzent Pink #F43F8A, heller Hintergrund,
 * dunkler Lila-Hero-Verlauf, lila-getönte Schatten, große Radien.
 */
object Brand {
    val Primary = Color(0xFF6C3AE0)
    val PrimaryLight = Color(0xFF8B5CF6)
    val PrimaryDark = Color(0xFF4A1FB0)
    val PrimaryMid = Color(0xFF5B2BC7)
    val Accent = Color(0xFFF43F8A)
    val AccentLight = Color(0xFFFF6BA8)
    val Cyan = Color(0xFF00D8FF)

    val Bg = Color(0xFFF8F9FC)
    val CardBg = Color(0xFFFFFFFF)
    val CardBgAlt = Color(0xFFF3F4F8)
    val TextStrong = Color(0xFF1A1D2E)
    val TextLight = Color(0xFF5E6278)
    val TextMuted = Color(0xFF9298AD)
    val Border = Color(0xFFE4E7F0)
    val BorderLight = Color(0xFFEDEEF5)
    val Success = Color(0xFF10B981)
    val Danger = Color(0xFFEF4444)

    // Verläufe
    val hero: Brush
        get() = Brush.linearGradient(
            0.00f to Color(0xFF080818),
            0.30f to Color(0xFF12102A),
            0.60f to Color(0xFF1E1448),
            0.85f to Color(0xFF3B1D72),
            1.00f to Primary,
            start = Offset(0f, 0f),
            end = Offset(0f, Float.POSITIVE_INFINITY),
        )

    val primaryGradient: Brush
        get() = Brush.linearGradient(listOf(Primary, PrimaryMid, PrimaryDark))

    val primaryToAccent: Brush
        get() = Brush.linearGradient(listOf(Primary, Accent))

    val accentGradient: Brush
        get() = Brush.linearGradient(listOf(Accent, AccentLight))
}
