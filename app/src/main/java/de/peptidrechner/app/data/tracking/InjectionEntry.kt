package de.peptidrechner.app.data.tracking

/** Ein protokollierter Injektions-Eintrag. */
data class InjectionEntry(
    val id: Long,
    val peptideName: String,
    /** Menschlich lesbare Dosis, z. B. "2 mg" oder "250 mcg". */
    val doseText: String,
    /** Aufgezogene Einheiten (IE, U-100). */
    val units: Double,
    /** Aufgezogenes Volumen in ml. */
    val ml: Double,
    /** Zeitpunkt in Millisekunden (System.currentTimeMillis). */
    val timestamp: Long,
)
