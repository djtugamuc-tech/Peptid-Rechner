package de.peptidrechner.app.ui

import java.util.Locale
import kotlin.math.abs

/** Zahl mit deutschem Komma, ohne unnötige Nachkommastellen. */
fun Double.fmt(maxDecimals: Int = 2): String {
    if (this.isNaN() || this.isInfinite()) return "–"
    val rounded = String.format(Locale.GERMANY, "%.${maxDecimals}f", this)
    // Trailing-Nullen entfernen
    return if (rounded.contains(',')) {
        rounded.trimEnd('0').trimEnd(',')
    } else rounded
}

/** Menge in mcg lesbar darstellen (schaltet ab 1000 mcg auf mg um). */
fun formatAmountMcg(mcg: Double): String {
    return if (abs(mcg) >= 1000.0) {
        "${(mcg / 1000.0).fmt(3)} mg"
    } else {
        "${mcg.fmt(1)} mcg"
    }
}
