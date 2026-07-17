package de.peptidrechner.app.data

/** Einheit, in der eine Dosis typischerweise angegeben wird. */
enum class DoseUnit(val label: String) {
    MCG("mcg"),
    MG("mg");

    /** Faktor zur Umrechnung in Mikrogramm. */
    val toMcg: Double
        get() = when (this) {
            MCG -> 1.0
            MG -> 1000.0
        }
}

/** Thematische Gruppierung der Peptide (analog zu den Kategorien der Wiki). */
enum class PeptideCategory(val title: String, val emoji: String) {
    ABNEHMEN("Abnehmen & Stoffwechsel", "⚖️"),
    HEILUNG("Heilung & Regeneration", "🩹"),
    WACHSTUM("Wachstumshormon (GH)", "💪"),
    KOSMETIK("Haut & Kosmetik", "✨"),
    KOGNITIV("Kognitiv & Nootropika", "🧠"),
    SEXUAL("Sexuelle Gesundheit", "❤️"),
    LONGEVITY("Longevity & Sonstige", "⏳");
}

/**
 * Ein Peptid mit sinnvollen Standardwerten für den Rechner.
 *
 * @param defaultVialMg   Übliche Wirkstoffmenge pro Fläschchen (Vial) in mg.
 * @param defaultWaterMl  Empfohlene Menge bakteriostatisches Wasser in ml.
 * @param defaultDose     Vorgeschlagene Einzeldosis (in der Einheit [doseUnit]).
 * @param doseUnit        Einheit, in der die Dosis für dieses Peptid üblicherweise angegeben wird.
 * @param doseRange       Kurztext mit dem typischen Dosisbereich (rein informativ).
 */
data class Peptide(
    val name: String,
    val aka: String? = null,
    val category: PeptideCategory,
    val defaultVialMg: Double,
    val defaultWaterMl: Double,
    val defaultDose: Double,
    val doseUnit: DoseUnit,
    val doseRange: String,
    val note: String? = null,
    /** true, wenn dieses Peptid üblicherweise auch als Nasenspray verwendet wird. */
    val nasal: Boolean = false,
) {
    val searchText: String
        get() = buildString {
            append(name.lowercase())
            aka?.let { append(' '); append(it.lowercase()) }
            append(' '); append(category.title.lowercase())
        }
}
