package de.peptidrechner.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import android.widget.Toast
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.peptidrechner.app.calc.ReconstitutionCalculator
import de.peptidrechner.app.data.tracking.TrackingRepository
import de.peptidrechner.app.calc.ReconstitutionInput
import de.peptidrechner.app.calc.ReconstitutionResult
import de.peptidrechner.app.calc.SyringeSize
import de.peptidrechner.app.data.DoseUnit
import de.peptidrechner.app.data.Peptide
import de.peptidrechner.app.ui.components.GlassCard
import de.peptidrechner.app.ui.components.Pill
import de.peptidrechner.app.ui.components.SyringeView
import de.peptidrechner.app.ui.fmt
import de.peptidrechner.app.ui.formatAmountMcg
import de.peptidrechner.app.ui.theme.AppC
import de.peptidrechner.app.ui.theme.Brand
import de.peptidrechner.app.ui.theme.JetMono

private fun String.parseNum(): Double? = replace(',', '.').trim().toDoubleOrNull()

@Composable
fun CalculatorScreen(
    peptide: Peptide,
    onChangePeptide: () -> Unit,
) {
    var vialMg by rememberSaveable(peptide.name) { mutableStateOf(peptide.defaultVialMg.fmt()) }
    var waterMl by rememberSaveable(peptide.name) { mutableStateOf(peptide.defaultWaterMl.fmt()) }
    var doseUnit by rememberSaveable(peptide.name) { mutableStateOf(peptide.doseUnit) }
    var dose by rememberSaveable(peptide.name) { mutableStateOf(peptide.defaultDose.fmt()) }
    var syringe by rememberSaveable(peptide.name) { mutableStateOf(SyringeSize.U100) }

    val doseMcg = (dose.parseNum() ?: 0.0) * doseUnit.toMcg
    val input = ReconstitutionInput(
        vialMg = vialMg.parseNum() ?: 0.0,
        waterMl = waterMl.parseNum() ?: 0.0,
        doseMcg = doseMcg,
        syringe = syringe,
    )
    val result = remember(input) { ReconstitutionCalculator.calculate(input) }
    val statusBar = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(Modifier.fillMaxSize().background(AppC.bg)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Hero: Ergebnis + Peptid-Auswahl
            ResultHero(
                peptide = peptide,
                result = result,
                syringe = syringe,
                doseMcg = doseMcg,
                topInset = statusBar,
                onChangePeptide = onChangePeptide,
            )

            Column(
                Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                StepLabel("1", "Fläschchen (Vial)")
                NumberInput("Peptid-Menge im Vial", vialMg, { vialMg = it }, "mg", listOf("5", "10", "15", "30"))

                StepLabel("2", "Bakteriostatisches Wasser")
                NumberInput(
                    "Zugegebenes Wasser", waterMl, { waterMl = it }, "ml", listOf("1", "2", "3", "5"),
                    hint = "💡 Empfehlung für ${peptide.name}: ${peptide.defaultWaterMl.fmt()} ml " +
                        "(ergibt ${formatAmountMcg(((vialMg.parseNum() ?: peptide.defaultVialMg) * 1000) / (peptide.defaultWaterMl))}/ml).",
                )

                StepLabel("3", "Gewünschte Dosis")
                GlassCard(radius = 20, padding = PaddingValues(16.dp), modifier = Modifier.fillMaxWidth()) {
                    UnitToggle(doseUnit) { doseUnit = it }
                    Spacer(Modifier.height(12.dp))
                    PlainNumberField("Dosis pro Injektion", dose, { dose = it }, doseUnit.label)
                    Spacer(Modifier.height(10.dp))
                    ChipRow(
                        presets = if (doseUnit == DoseUnit.MCG) listOf("100", "250", "500", "1000")
                        else listOf("0,25", "0,5", "1", "2,5"),
                        value = dose,
                        suffix = doseUnit.label,
                        onPick = { dose = it },
                    )
                }

                StepLabel("4", "Spritze (U-100)")
                GlassCard(radius = 20, padding = PaddingValues(16.dp), modifier = Modifier.fillMaxWidth()) {
                    SyringeSelector(syringe) { syringe = it }
                }

                if (peptide.nasal) {
                    NasalCard(result = result, doseMcg = doseMcg)
                }

                HowToCard(result, peptide, doseMcg)
                DisclaimerCard()
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ResultHero(
    peptide: Peptide,
    result: ReconstitutionResult?,
    syringe: SyringeSize,
    doseMcg: Double,
    topInset: androidx.compose.ui.unit.Dp,
    onChangePeptide: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(Brand.hero)
            .padding(top = topInset + 20.dp, bottom = 26.dp, start = 20.dp, end = 20.dp),
    ) {
        Column {
            // Peptid-Auswahlzeile
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.10f))
                    .clickable(onClick = onChangePeptide)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                GradientAvatar(peptide.name.take(1).uppercase(), size = 40)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(peptide.name, style = MaterialTheme.typography.titleMedium, color = Color.White)
                    Text(
                        peptide.category.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f),
                    )
                }
                Pill("Ändern", bg = Color.White.copy(alpha = 0.18f), fg = Color.White)
                Spacer(Modifier.width(6.dp))
                Icon(Icons.Default.Edit, null, tint = Color.White.copy(alpha = 0.85f), modifier = Modifier.size(18.dp))
            }

            Spacer(Modifier.height(20.dp))
            Text(
                "AUFZIEHEN BIS",
                style = MaterialTheme.typography.labelMedium,
                color = Brand.AccentLight,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            if (result == null) {
                Text("—", fontFamily = JetMono, fontSize = 56.sp, color = Color.White, fontWeight = FontWeight.Bold)
                Text("Bitte alle Werte eingeben.", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.7f))
            } else {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        result.drawUnits.fmt(1),
                        fontFamily = JetMono,
                        fontSize = 60.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "IE",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Brand.AccentLight,
                        modifier = Modifier.padding(bottom = 10.dp),
                    )
                }
                Text(
                    "= ${result.drawMl.fmt(3)} ml  ·  ${formatAmountMcg(doseMcg)} pro Dosis",
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = JetMono,
                    color = Color.White.copy(alpha = 0.85f),
                )

                Spacer(Modifier.height(18.dp))
                SyringeView(
                    fillUnits = result.drawUnits,
                    maxUnits = syringe.units,
                    barrelColor = Color.White.copy(alpha = 0.92f),
                    liquidColor = Brand.AccentLight,
                    liquidColorEnd = Brand.Cyan,
                    tickColor = Color.White.copy(alpha = 0.45f),
                    labelColor = Color.White.copy(alpha = 0.75f),
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text("Skala in IE", fontFamily = JetMono, fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                }

                if (result.exceedsSyringe) {
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, null, tint = Brand.AccentLight, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Passt nicht in ${syringe.label}. Mehr Wasser oder größere Spritze wählen.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Brand.AccentLight,
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    HeroStat("Konzentration", formatAmountMcg(result.concentrationMcgPerMl), "pro ml", Modifier.weight(1f))
                    HeroStat("Dosen / Vial", "≈ ${result.dosesPerVial.fmt(0)}", "Injektionen", Modifier.weight(1f))
                }

                Spacer(Modifier.height(16.dp))
                val context = LocalContext.current
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .clickable(enabled = !result.exceedsSyringe) {
                            TrackingRepository.add(
                                peptideName = peptide.name,
                                doseText = formatAmountMcg(doseMcg),
                                units = result.drawUnits,
                                ml = result.drawMl,
                                timestamp = System.currentTimeMillis(),
                            )
                            Toast.makeText(context, "Injektion protokolliert ✓", Toast.LENGTH_SHORT).show()
                        }
                        .padding(vertical = 15.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, null, tint = Brand.Primary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Injektion protokollieren",
                            style = MaterialTheme.typography.labelLarge,
                            color = Brand.Primary,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroStat(label: String, value: String, unit: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.10f))
            .padding(14.dp),
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.7f))
        Spacer(Modifier.height(4.dp))
        Text(value, fontFamily = JetMono, fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
        Text(unit, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.6f))
    }
}

@Composable
private fun StepLabel(number: String, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(RoundedCornerShape(50))
                .background(Brand.primaryToAccent),
            contentAlignment = Alignment.Center,
        ) {
            Text(number, fontFamily = JetMono, fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(10.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, color = AppC.textStrong, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun NumberInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    suffix: String,
    presets: List<String>,
    hint: String? = null,
) {
    GlassCard(radius = 20, padding = PaddingValues(16.dp), modifier = Modifier.fillMaxWidth()) {
        PlainNumberField(label, value, onValueChange, suffix)
        Spacer(Modifier.height(10.dp))
        ChipRow(presets, value, suffix, onValueChange)
        if (hint != null) {
            Spacer(Modifier.height(10.dp))
            Text(hint, style = MaterialTheme.typography.bodyMedium, color = Brand.Primary)
        }
    }
}

@Composable
private fun PlainNumberField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    suffix: String,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        suffix = { Text(suffix, fontFamily = JetMono, color = AppC.textLight) },
        singleLine = true,
        textStyle = MaterialTheme.typography.titleLarge.copy(fontFamily = JetMono),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Brand.Primary,
            unfocusedBorderColor = AppC.border,
            focusedLabelColor = Brand.Primary,
            cursorColor = Brand.Primary,
            focusedContainerColor = AppC.cardBg,
            unfocusedContainerColor = AppC.cardBg,
        ),
    )
}

@Composable
private fun ChipRow(presets: List<String>, value: String, suffix: String, onPick: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        presets.forEach { preset ->
            val selected = value == preset
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selected) Brand.Primary else AppC.cardBgAlt)
                    .clickable { onPick(preset) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    preset,
                    fontFamily = JetMono,
                    fontSize = 13.sp,
                    color = if (selected) Color.White else AppC.textLight,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun UnitToggle(selected: DoseUnit, onSelected: (DoseUnit) -> Unit) {
    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        DoseUnit.entries.forEachIndexed { i, unit ->
            SegmentedButton(
                selected = selected == unit,
                onClick = { onSelected(unit) },
                shape = SegmentedButtonDefaults.itemShape(i, DoseUnit.entries.size),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = Brand.Primary,
                    activeContentColor = Color.White,
                ),
            ) { Text(unit.label, fontFamily = JetMono) }
        }
    }
}

@Composable
private fun SyringeSelector(selected: SyringeSize, onSelected: (SyringeSize) -> Unit) {
    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        SyringeSize.entries.forEachIndexed { i, size ->
            SegmentedButton(
                selected = selected == size,
                onClick = { onSelected(size) },
                shape = SegmentedButtonDefaults.itemShape(i, SyringeSize.entries.size),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = Brand.Primary,
                    activeContentColor = Color.White,
                ),
            ) { Text(size.label, fontSize = 12.sp) }
        }
    }
}

@Composable
private fun HowToCard(result: ReconstitutionResult?, peptide: Peptide, doseMcg: Double) {
    GlassCard(radius = 20, padding = PaddingValues(18.dp), modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, null, tint = Brand.Primary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("So gehst du vor", style = MaterialTheme.typography.titleMedium, color = AppC.textStrong, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        Step(1, "Spritze das bakteriostatische Wasser langsam an der Glaswand ins Peptid-Fläschchen.")
        Step(2, "Nicht schütteln – vorsichtig schwenken, bis sich alles gelöst hat.")
        if (result != null) {
            Step(3, "Ziehe die Spritze bis zur Markierung ${result.drawUnits.fmt(1)} IE (= ${result.drawMl.fmt(3)} ml) auf. Das sind ${formatAmountMcg(doseMcg)}.")
            Step(4, "Subkutan injizieren. Ein ${peptide.defaultVialMg.fmt()}-mg-Vial reicht für ca. ${result.dosesPerVial.fmt(0)} Dosen.")
        } else {
            Step(3, "Sobald alle Werte gesetzt sind, erscheint hier die genaue Einheiten-Markierung.")
        }
        Step(5, "Rekonstituiertes Peptid im Kühlschrank lagern.")
    }
}

@Composable
private fun NasalCard(result: ReconstitutionResult?, doseMcg: Double) {
    // Standard-Nasenpumpe gibt ca. 0,1 ml pro Sprühstoß ab.
    val perSprayMl = 0.1
    GlassCard(radius = 20, padding = PaddingValues(18.dp), modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🫧", fontSize = 18.sp)
            Spacer(Modifier.width(8.dp))
            Text("Als Nasenspray", style = MaterialTheme.typography.titleMedium, color = AppC.textStrong, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            Pill("möglich")
        }
        Spacer(Modifier.height(12.dp))

        if (result != null && result.concentrationMcgPerMl > 0) {
            val perSprayMcg = result.concentrationMcgPerMl * perSprayMl
            val sprays = if (perSprayMcg > 0) (doseMcg / perSprayMcg) else 0.0
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NasalStat("Pro Sprühstoß", formatAmountMcg(perSprayMcg), "≈ 0,1 ml", Modifier.weight(1f))
                NasalStat("Für deine Dosis", "${sprays.fmt(1)}×", "Sprühstöße", Modifier.weight(1f))
            }
            Spacer(Modifier.height(14.dp))
        }

        Text("So mischst du das Nasenspray", style = MaterialTheme.typography.labelMedium, color = Brand.Primary, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Step(1, "Peptid wie oben mit bakteriostatischem Wasser rekonstituieren (Zielkonzentration meist ~1 mg/ml).")
        Step(2, "Die Lösung in eine leere, sterile Nasenspray-Flasche (z. B. 10 ml) umfüllen.")
        Step(3, "Vor dem ersten Gebrauch 3–4× in die Luft pumpen, bis ein feiner Sprühnebel kommt.")
        Step(4, "Kopf leicht nach vorn, ein Nasenloch zuhalten, beim Sprühen sanft einatmen – nicht hochschniefen.")
        Step(5, "Zwischen den Sprühstößen kurz warten; Flasche im Kühlschrank lagern (~28 Tage haltbar).")
    }
}

@Composable
private fun NasalStat(label: String, value: String, unit: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Brand.Primary.copy(alpha = 0.08f))
            .padding(14.dp),
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = AppC.textMuted)
        Spacer(Modifier.height(2.dp))
        Text(value, fontFamily = JetMono, fontSize = 20.sp, color = Brand.Primary, fontWeight = FontWeight.Bold)
        Text(unit, style = MaterialTheme.typography.bodyMedium, color = AppC.textLight)
    }
}

@Composable
private fun Step(number: Int, text: String) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 4.dp)) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(50))
                .background(Brand.Primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Text("$number", fontFamily = JetMono, fontSize = 12.sp, color = Brand.Primary, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge, color = AppC.textLight, modifier = Modifier.padding(top = 1.dp))
    }
}

@Composable
private fun DisclaimerCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brand.Danger.copy(alpha = 0.08f))
            .padding(14.dp),
    ) {
        Text(
            "⚠️ Reines Rechen- und Bildungswerkzeug für Forschungszwecke – keine medizinische Beratung. " +
                "Peptide sind teils nicht als Arzneimittel zugelassen. Vor jeder Anwendung mit Fachpersonal sprechen.",
            style = MaterialTheme.typography.bodyMedium,
            color = Brand.Danger,
        )
    }
}
