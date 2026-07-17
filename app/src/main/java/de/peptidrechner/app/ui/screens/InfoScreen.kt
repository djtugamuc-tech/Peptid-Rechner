package de.peptidrechner.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.peptidrechner.app.ui.components.GlassCard
import de.peptidrechner.app.ui.theme.Brand
import de.peptidrechner.app.ui.theme.JetMono

@Composable
fun InfoScreen() {
    val statusBar = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    Box(Modifier.fillMaxSize().background(Brand.Bg)) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(Brand.hero)
                    .padding(top = statusBar + 28.dp, bottom = 24.dp, start = 20.dp, end = 20.dp),
            ) {
                Column {
                    Text("So rechnet die App", style = MaterialTheme.typography.displaySmall, color = Color.White)
                    Spacer(Modifier.height(4.dp))
                    Text("Die Formel & Hinweise", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.72f))
                }
            }

            Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                GlassCard(radius = 20, padding = PaddingValues(18.dp), modifier = Modifier.fillMaxWidth()) {
                    Text("Rekonstitutions-Formel", style = MaterialTheme.typography.titleMedium, color = Brand.TextStrong, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    Formula("Konzentration", "Vial (mg) × 1000 ÷ Wasser (ml)")
                    Formula("Volumen (ml)", "Dosis (mcg) ÷ Konzentration")
                    Formula("Einheiten (IE)", "Volumen (ml) × 100   (U-100)")
                    Formula("Dosen / Vial", "Vial (mcg) ÷ Dosis (mcg)")
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Beispiel: 10 mg Vial + 2 ml Wasser, Dosis 2 mg → 5 mg/ml → 0,4 ml = 40 IE (~5 Dosen).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Brand.TextLight,
                    )
                }

                GlassCard(radius = 20, padding = PaddingValues(18.dp), modifier = Modifier.fillMaxWidth()) {
                    Text("U-100 Insulinspritze", style = MaterialTheme.typography.titleMedium, color = Brand.TextStrong, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "U-100 bedeutet: 100 Einheiten (IE) = 1 ml. Die App zeigt dir immer, bis zu " +
                            "welcher IE-Markierung du aufziehst – unabhängig von der Spritzengröße.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Brand.TextLight,
                    )
                }

                GlassCard(radius = 20, padding = PaddingValues(18.dp), modifier = Modifier.fillMaxWidth()) {
                    Text("💧 Bakteriostatisches Wasser (BAC)", style = MaterialTheme.typography.titleMedium, color = Brand.TextStrong, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "BAC-Wasser ist steriles Wasser mit 0,9 % Benzylalkohol als Konservierungsmittel – " +
                            "damit ist die gelöste Lösung ca. 28 Tage bei 2–8 °C (Kühlschrank) haltbar.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Brand.TextLight,
                    )
                    Spacer(Modifier.height(10.dp))
                    Bullet("Es gibt keinen festen »Pflichtwert« pro Peptid – die Wassermenge wählst du, um eine praktische Konzentration zu erhalten.")
                    Bullet("Gängiger Richtwert: 5 mg Vial + 2 ml → 2,5 mg/ml. Jedes Peptid hat in der App einen passenden Vorschlag hinterlegt.")
                    Bullet("Mehr Wasser = kleinere Dosis pro IE (feinere Dosierung), weniger Wasser = umgekehrt.")
                    Bullet("Nasal-Peptide (Semax/Selank) werden meist auf ~1 mg/ml gemischt.")
                }

                GlassCard(radius = 20, padding = PaddingValues(18.dp), modifier = Modifier.fillMaxWidth()) {
                    Text("💉 Welche Spritze kaufen?", style = MaterialTheme.typography.titleMedium, color = Brand.TextStrong, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Für subkutane Injektionen empfehlen Nutzer-Communities U-100 " +
                            "Insulinspritzen mit fest verbauter Nadel.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Brand.TextLight,
                    )
                    Spacer(Modifier.height(12.dp))

                    Text("Einsteiger-Standard", style = MaterialTheme.typography.labelMedium, color = Brand.Primary, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Spec("Größe", "0,5 ml (50 IE)")
                    Spec("Nadelstärke", "30 G (guter Allrounder)")
                    Spec("Nadellänge", "8 mm (5/16\")")
                    Spec("Skala", "U-100, 1-IE-Schritte")

                    Spacer(Modifier.height(14.dp))
                    Text("Größe nach Dosis wählen", style = MaterialTheme.typography.labelMedium, color = Brand.Primary, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Bullet("0,3 ml – kleine Dosen (bis 30 IE): jede Linie = 1 IE, beste Ablesung")
                    Bullet("0,5 ml – Standard für die meisten Dosen (bis 50 IE)")
                    Bullet("1,0 ml – große Volumen (über 50 IE)")

                    Spacer(Modifier.height(14.dp))
                    Text("Nadelstärke (Gauge)", style = MaterialTheme.typography.labelMedium, color = Brand.Primary, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Bullet("29–31 G üblich · höhere Zahl = dünner = weniger Schmerz")
                    Bullet("31 G am feinsten · bei sehr schlanken Personen 4–6 mm Länge")

                    Spacer(Modifier.height(12.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brand.Primary.copy(alpha = 0.08f))
                            .padding(12.dp),
                    ) {
                        Text(
                            "Faustregel: die kleinste Spritze wählen, in die deine Dosis passt – " +
                                "das gibt die genaueste Ablesung. Immer sterile Einwegspritzen, " +
                                "danach im Sharps-Behälter entsorgen.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Brand.TextStrong,
                        )
                    }
                }

                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brand.Danger.copy(alpha = 0.08f))
                        .padding(16.dp),
                ) {
                    Text(
                        "⚠️ Reines Rechen- und Bildungswerkzeug für Forschungszwecke. Keine medizinische " +
                            "Beratung, keine Dosierempfehlung. Peptide sind teils nicht als Arzneimittel " +
                            "zugelassen. Vor jeder Anwendung medizinisches Fachpersonal konsultieren.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Brand.Danger,
                    )
                }

                Text(
                    "Daten angelehnt an peptidwiki.de · Rechenlogik nach particlepeptides.com · Design-Inspiration biohk.de",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Brand.TextMuted,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun Formula(label: String, expr: String) {
    Column(Modifier.padding(vertical = 5.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = Brand.Primary, fontWeight = FontWeight.Bold)
        Text(expr, fontFamily = JetMono, color = Brand.TextStrong, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun Spec(label: String, value: String) {
    androidx.compose.foundation.layout.Row(
        Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Brand.TextMuted)
        Text(value, fontFamily = JetMono, color = Brand.TextStrong, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun Bullet(text: String) {
    androidx.compose.foundation.layout.Row(Modifier.padding(vertical = 3.dp)) {
        Text("•  ", color = Brand.Primary, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        Text(text, style = MaterialTheme.typography.bodyMedium, color = Brand.TextLight)
    }
}
