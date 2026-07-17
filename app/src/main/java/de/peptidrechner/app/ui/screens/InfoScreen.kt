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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.peptidrechner.app.ui.components.GlassCard
import de.peptidrechner.app.ui.components.Pill
import de.peptidrechner.app.update.UpdateChecker
import de.peptidrechner.app.update.UpdateInfo
import de.peptidrechner.app.ui.theme.AppC
import de.peptidrechner.app.ui.theme.Brand
import de.peptidrechner.app.ui.theme.JetMono
import kotlinx.coroutines.launch

@Composable
fun InfoScreen() {
    val statusBar = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    Box(Modifier.fillMaxSize().background(AppC.bg)) {
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
                UpdateCard()

                GlassCard(radius = 20, padding = PaddingValues(18.dp), modifier = Modifier.fillMaxWidth()) {
                    Text("Rekonstitutions-Formel", style = MaterialTheme.typography.titleMedium, color = AppC.textStrong, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    Formula("Konzentration", "Vial (mg) × 1000 ÷ Wasser (ml)")
                    Formula("Volumen (ml)", "Dosis (mcg) ÷ Konzentration")
                    Formula("Einheiten (IE)", "Volumen (ml) × 100   (U-100)")
                    Formula("Dosen / Vial", "Vial (mcg) ÷ Dosis (mcg)")
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Beispiel: 10 mg Vial + 2 ml Wasser, Dosis 2 mg → 5 mg/ml → 0,4 ml = 40 IE (~5 Dosen).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppC.textLight,
                    )
                }

                GlassCard(radius = 20, padding = PaddingValues(18.dp), modifier = Modifier.fillMaxWidth()) {
                    Text("U-100 Insulinspritze", style = MaterialTheme.typography.titleMedium, color = AppC.textStrong, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "U-100 bedeutet: 100 Einheiten (IE) = 1 ml. Die App zeigt dir immer, bis zu " +
                            "welcher IE-Markierung du aufziehst – unabhängig von der Spritzengröße.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = AppC.textLight,
                    )
                }

                GlassCard(radius = 20, padding = PaddingValues(18.dp), modifier = Modifier.fillMaxWidth()) {
                    Text("💧 Bakteriostatisches Wasser (BAC)", style = MaterialTheme.typography.titleMedium, color = AppC.textStrong, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "BAC-Wasser ist steriles Wasser mit 0,9 % Benzylalkohol als Konservierungsmittel – " +
                            "damit ist die gelöste Lösung ca. 28 Tage bei 2–8 °C (Kühlschrank) haltbar.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = AppC.textLight,
                    )
                    Spacer(Modifier.height(10.dp))
                    Bullet("Es gibt keinen festen »Pflichtwert« pro Peptid – die Wassermenge wählst du, um eine praktische Konzentration zu erhalten.")
                    Bullet("Gängiger Richtwert: 5 mg Vial + 2 ml → 2,5 mg/ml. Jedes Peptid hat in der App einen passenden Vorschlag hinterlegt.")
                    Bullet("Mehr Wasser = kleinere Dosis pro IE (feinere Dosierung), weniger Wasser = umgekehrt.")
                    Bullet("Nasal-Peptide (Semax/Selank) werden meist auf ~1 mg/ml gemischt.")
                }

                GlassCard(radius = 20, padding = PaddingValues(18.dp), modifier = Modifier.fillMaxWidth()) {
                    Text("💉 Welche Spritze kaufen?", style = MaterialTheme.typography.titleMedium, color = AppC.textStrong, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Für subkutane Injektionen empfehlen Nutzer-Communities U-100 " +
                            "Insulinspritzen mit fest verbauter Nadel.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppC.textLight,
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
                            color = AppC.textStrong,
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
                    "Daten angelehnt an peptidwiki.de · Rechenlogik nach gängigen Rekonstitutions-Beispielen",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppC.textMuted,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun UpdateCard() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val current = remember { UpdateChecker.currentVersion(context) }
    var loading by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf<String?>(null) }
    var update by remember { mutableStateOf<UpdateInfo?>(null) }

    GlassCard(radius = 20, padding = PaddingValues(18.dp), modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("⬇️ App-Update", style = MaterialTheme.typography.titleMedium, color = AppC.textStrong, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            Pill("v$current")
        }
        Spacer(Modifier.height(6.dp))
        Text(
            "Prüft automatisch die neueste Version auf GitHub und installiert sie auf Wunsch.",
            style = MaterialTheme.typography.bodyMedium,
            color = AppC.textLight,
        )
        Spacer(Modifier.height(14.dp))

        // Button „Auf Updates prüfen"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(if (loading) AppC.cardBgAlt else Brand.Primary)
                .clickable(enabled = !loading) {
                    loading = true; status = null; update = null
                    scope.launch {
                        val info = UpdateChecker.fetchLatest(context)
                        loading = false
                        when {
                            info == null -> status = "Prüfung fehlgeschlagen – bist du online?"
                            info.isNewer -> { update = info; status = "Neue Version verfügbar: ${info.version}" }
                            else -> status = "Du hast die neueste Version ✓"
                        }
                    }
                }
                .padding(vertical = 13.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (loading) {
                CircularProgressIndicator(Modifier.size(18.dp), color = Brand.Primary, strokeWidth = 2.dp)
                Spacer(Modifier.width(10.dp))
                Text("Prüfe …", color = AppC.textLight, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
            } else {
                Icon(Icons.Default.Refresh, null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Auf Updates prüfen", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
            }
        }

        status?.let {
            Spacer(Modifier.height(10.dp))
            Text(it, style = MaterialTheme.typography.bodyMedium, color = if (update != null) Brand.Primary else AppC.textLight, fontWeight = FontWeight.SemiBold)
        }

        update?.let { info ->
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brand.primaryToAccent)
                    .clickable { UpdateChecker.downloadAndInstall(context, info.downloadUrl) }
                    .padding(vertical = 13.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("Jetzt aktualisieren", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun Formula(label: String, expr: String) {
    Column(Modifier.padding(vertical = 5.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = Brand.Primary, fontWeight = FontWeight.Bold)
        Text(expr, fontFamily = JetMono, color = AppC.textStrong, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun Spec(label: String, value: String) {
    androidx.compose.foundation.layout.Row(
        Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = AppC.textMuted)
        Text(value, fontFamily = JetMono, color = AppC.textStrong, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun Bullet(text: String) {
    androidx.compose.foundation.layout.Row(Modifier.padding(vertical = 3.dp)) {
        Text("•  ", color = Brand.Primary, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        Text(text, style = MaterialTheme.typography.bodyMedium, color = AppC.textLight)
    }
}
