package de.peptidrechner.app.calc

/**
 * Insulinspritzen-Größen (U-100 Standard: 100 Einheiten = 1 ml).
 */
enum class SyringeSize(val ml: Double, val units: Int, val label: String) {
    U30(0.3, 30, "0,3 ml (30 IE)"),
    U50(0.5, 50, "0,5 ml (50 IE)"),
    U100(1.0, 100, "1,0 ml (100 IE)");
}

/** Eingaben für die Rekonstitutions-Berechnung. */
data class ReconstitutionInput(
    /** Wirkstoffmenge im Fläschchen in mg. */
    val vialMg: Double,
    /** Zugegebenes bakteriostatisches Wasser in ml. */
    val waterMl: Double,
    /** Gewünschte Einzeldosis in Mikrogramm (mcg). */
    val doseMcg: Double,
    /** Gewählte Spritze (U-100). */
    val syringe: SyringeSize,
)

/** Ergebnis der Berechnung. */
data class ReconstitutionResult(
    /** Konzentration in mcg pro ml. */
    val concentrationMcgPerMl: Double,
    /** Zu ziehendes Volumen in ml. */
    val drawMl: Double,
    /** Zu ziehende Einheiten auf einer U-100-Spritze. */
    val drawUnits: Double,
    /** Anzahl möglicher Dosen pro Fläschchen. */
    val dosesPerVial: Double,
    /** true, wenn die Dosis nicht in die gewählte Spritze passt. */
    val exceedsSyringe: Boolean,
    /** Anteil der gewählten Spritze, der gefüllt wird (0..1+). */
    val fillFraction: Double,
)

object ReconstitutionCalculator {

    /** U-100-Standard: 100 Einheiten pro Milliliter. */
    private const val UNITS_PER_ML = 100.0

    fun calculate(input: ReconstitutionInput): ReconstitutionResult? {
        if (input.vialMg <= 0.0 || input.waterMl <= 0.0 || input.doseMcg <= 0.0) {
            return null
        }

        val totalMcg = input.vialMg * 1000.0
        val concentration = totalMcg / input.waterMl          // mcg / ml
        val drawMl = input.doseMcg / concentration            // ml
        val drawUnits = drawMl * UNITS_PER_ML                 // IE auf U-100
        val dosesPerVial = totalMcg / input.doseMcg
        val fillFraction = drawUnits / input.syringe.units
        val exceeds = drawUnits > input.syringe.units + 1e-9

        return ReconstitutionResult(
            concentrationMcgPerMl = concentration,
            drawMl = drawMl,
            drawUnits = drawUnits,
            dosesPerVial = dosesPerVial,
            exceedsSyringe = exceeds,
            fillFraction = fillFraction,
        )
    }
}
