package de.peptidrechner.app.data

import de.peptidrechner.app.data.DoseUnit.MCG
import de.peptidrechner.app.data.DoseUnit.MG
import de.peptidrechner.app.data.PeptideCategory.ABNEHMEN
import de.peptidrechner.app.data.PeptideCategory.HEILUNG
import de.peptidrechner.app.data.PeptideCategory.KOGNITIV
import de.peptidrechner.app.data.PeptideCategory.KOSMETIK
import de.peptidrechner.app.data.PeptideCategory.LONGEVITY
import de.peptidrechner.app.data.PeptideCategory.SEXUAL
import de.peptidrechner.app.data.PeptideCategory.WACHSTUM

/**
 * Kuratierte Peptid-Datenbank, angelehnt an peptidwiki.de.
 *
 * Standardwerte (Vial-Größe, Wassermenge, Einzeldosis) sind gängige Startwerte
 * aus Rekonstitutions-Beispielen und dienen nur der Voreinstellung – der
 * Nutzer kann jeden Wert im Rechner frei anpassen.
 *
 * Neue Peptide lassen sich einfach durch Ergänzen der Liste hinzufügen.
 */
object PeptideRepository {

    val peptides: List<Peptide> = listOf(
        // --- Abnehmen & Stoffwechsel (GLP-1 / GIP / Glukagon) ---
        Peptide("Retatrutid", "LY3437943", ABNEHMEN, 10.0, 2.0, 2.0, MG, "2–12 mg / Woche",
            "GLP-1 / GIP / Glukagon Triple-Agonist"),
        Peptide("Semaglutid", "Ozempic, Wegovy", ABNEHMEN, 5.0, 2.0, 0.25, MG, "0,25–2,4 mg / Woche"),
        Peptide("Tirzepatid", "Mounjaro, Zepbound", ABNEHMEN, 10.0, 2.0, 2.5, MG, "2,5–15 mg / Woche"),
        Peptide("Liraglutid", "Victoza, Saxenda", ABNEHMEN, 18.0, 3.0, 0.6, MG, "0,6–3,0 mg / Tag"),
        Peptide("Cagrilintid", null, ABNEHMEN, 10.0, 2.0, 0.3, MG, "0,3–4,5 mg / Woche"),
        Peptide("Survodutid", "BI 456906", ABNEHMEN, 10.0, 2.0, 0.6, MG, "0,6–6,0 mg / Woche"),
        Peptide("Mazdutid", "IBI362", ABNEHMEN, 10.0, 2.0, 3.0, MG, "3–9 mg / Woche"),
        Peptide("AOD-9604", null, ABNEHMEN, 5.0, 2.0, 300.0, MCG, "250–500 mcg / Tag"),
        Peptide("Tesofensin", null, ABNEHMEN, 5.0, 2.0, 500.0, MCG, "250–1000 mcg / Tag"),
        Peptide("Fragment 176-191", "HGH Frag", ABNEHMEN, 5.0, 2.0, 250.0, MCG, "250–500 mcg / Dosis"),

        // --- Heilung & Regeneration ---
        Peptide("BPC-157", null, HEILUNG, 5.0, 2.0, 250.0, MCG, "200–500 mcg / Tag"),
        Peptide("TB-500", "Thymosin Beta-4", HEILUNG, 5.0, 2.0, 2000.0, MCG, "2–5 mg / Woche"),
        Peptide("BPC-157 + TB-500", "Wolverine", HEILUNG, 10.0, 3.0, 500.0, MCG, "kombiniert"),
        Peptide("KPV", null, HEILUNG, 5.0, 2.0, 250.0, MCG, "200–500 mcg / Tag"),
        Peptide("Thymosin Alpha-1", "Tα1", HEILUNG, 5.0, 2.0, 1500.0, MCG, "1,5 mg 2×/Woche"),
        Peptide("ARA-290", "Cibinetid", HEILUNG, 4.0, 2.0, 2000.0, MCG, "2–4 mg / Tag"),
        Peptide("GHK-Cu", "Kupferpeptid", HEILUNG, 50.0, 5.0, 2000.0, MCG, "1–3 mg / Tag"),
        Peptide("Larazotid", null, HEILUNG, 5.0, 2.0, 500.0, MCG, "250–500 mcg / Dosis"),

        // --- Wachstumshormon (GH-Sekretagoga) ---
        Peptide("CJC-1295 (ohne DAC)", "Mod GRF 1-29", WACHSTUM, 5.0, 2.0, 100.0, MCG, "100 mcg / Dosis"),
        Peptide("CJC-1295 DAC", null, WACHSTUM, 5.0, 2.0, 1000.0, MCG, "1–2 mg / Woche"),
        Peptide("Ipamorelin", null, WACHSTUM, 5.0, 2.0, 200.0, MCG, "200–300 mcg / Dosis"),
        Peptide("CJC-1295 + Ipamorelin", null, WACHSTUM, 10.0, 3.0, 200.0, MCG, "kombiniert"),
        Peptide("Sermorelin", null, WACHSTUM, 5.0, 2.0, 200.0, MCG, "100–300 mcg / Tag"),
        Peptide("Tesamorelin", "Egrifta", WACHSTUM, 5.0, 2.0, 1000.0, MCG, "1–2 mg / Tag"),
        Peptide("GHRP-2", null, WACHSTUM, 5.0, 2.0, 100.0, MCG, "100–300 mcg / Dosis"),
        Peptide("GHRP-6", null, WACHSTUM, 5.0, 2.0, 100.0, MCG, "100–300 mcg / Dosis"),
        Peptide("Hexarelin", null, WACHSTUM, 5.0, 2.0, 100.0, MCG, "100 mcg / Dosis"),
        Peptide("MK-677", "Ibutamoren", WACHSTUM, 5.0, 2.0, 100.0, MCG, "meist oral 10–25 mg"),
        Peptide("IGF-1 LR3", null, WACHSTUM, 1.0, 2.0, 40.0, MCG, "20–50 mcg / Tag"),

        // --- Haut & Kosmetik ---
        Peptide("Melanotan I", "Afamelanotid", KOSMETIK, 10.0, 2.0, 500.0, MCG, "0,5–1 mg / Tag"),
        Peptide("Melanotan II", "MT-2", KOSMETIK, 10.0, 2.0, 250.0, MCG, "250–500 mcg / Tag"),
        Peptide("SNAP-8", null, KOSMETIK, 10.0, 5.0, 500.0, MCG, "topisch"),
        Peptide("GHK-Cu (Kosmetik)", "Kupferpeptid", KOSMETIK, 50.0, 5.0, 2000.0, MCG, "topisch / s.c."),

        // --- Kognitiv & Nootropika ---
        Peptide("Semax", null, KOGNITIV, 5.0, 5.0, 300.0, MCG, "200–600 mcg / Tag",
            note = "Klassisches Nasenspray-Peptid (Nootropikum).", nasal = true),
        Peptide("Selank", null, KOGNITIV, 5.0, 5.0, 300.0, MCG, "200–600 mcg / Tag",
            note = "Klassisches Nasenspray-Peptid (Anxiolytikum).", nasal = true),
        Peptide("Dihexa", null, KOGNITIV, 5.0, 2.0, 100.0, MCG, "10–50 mg oral"),
        Peptide("Cerebrolysin", null, KOGNITIV, 5.0, 2.0, 1000.0, MCG, "1–5 ml / Tag"),
        Peptide("P21", "P021", KOGNITIV, 5.0, 2.0, 500.0, MCG, "500 mcg / Tag"),

        // --- Sexuelle Gesundheit ---
        Peptide("PT-141", "Bremelanotid", SEXUAL, 10.0, 2.0, 1000.0, MCG, "0,5–2 mg / Dosis",
            note = "Auch als Nasenspray verwendet.", nasal = true),
        Peptide("Kisspeptin-10", null, SEXUAL, 5.0, 2.0, 100.0, MCG, "50–200 mcg / Dosis"),
        Peptide("Gonadorelin", null, SEXUAL, 5.0, 2.0, 100.0, MCG, "100 mcg / Dosis"),
        Peptide("Oxytocin", null, SEXUAL, 5.0, 2.0, 20.0, MCG, "10–40 mcg / Dosis"),

        // --- Longevity & Sonstige ---
        Peptide("Epithalon", "Epitalon", LONGEVITY, 10.0, 2.0, 5000.0, MCG, "5–10 mg / Tag (Kur)"),
        Peptide("Thymalin", null, LONGEVITY, 10.0, 2.0, 5000.0, MCG, "5–10 mg / Tag (Kur)"),
        Peptide("MOTS-c", null, LONGEVITY, 10.0, 2.0, 5000.0, MCG, "5–10 mg / Woche"),
        Peptide("NAD+", null, LONGEVITY, 100.0, 3.0, 50000.0, MCG, "25–100 mg / Tag"),
        Peptide("5-Amino-1MQ", null, LONGEVITY, 50.0, 3.0, 50000.0, MCG, "50–150 mg / Tag"),
        Peptide("SS-31", "Elamipretid", LONGEVITY, 10.0, 2.0, 5000.0, MCG, "5–10 mg / Tag"),
        Peptide("Humanin", null, LONGEVITY, 5.0, 2.0, 1000.0, MCG, "1–5 mg / Dosis"),
        Peptide("DSIP", null, LONGEVITY, 5.0, 2.0, 250.0, MCG, "100–300 mcg vor dem Schlaf"),
        Peptide("Follistatin", "FST-344", LONGEVITY, 1.0, 2.0, 100.0, MCG, "100 mcg / Tag"),
        Peptide("Adipotid", "FTPP", LONGEVITY, 10.0, 2.0, 500.0, MCG, "0,5–1 mg / Dosis"),
    )

    val byCategory: Map<PeptideCategory, List<Peptide>>
        get() = peptides.groupBy { it.category }

    fun search(query: String): List<Peptide> {
        if (query.isBlank()) return peptides
        val q = query.trim().lowercase()
        return peptides.filter { it.searchText.contains(q) }
    }
}
