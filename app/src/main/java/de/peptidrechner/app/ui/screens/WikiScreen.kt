package de.peptidrechner.app.ui.screens

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.peptidrechner.app.data.Peptide
import de.peptidrechner.app.data.PeptideRepository
import de.peptidrechner.app.ui.components.GlassCard
import de.peptidrechner.app.ui.components.Pill
import de.peptidrechner.app.ui.theme.Brand
import de.peptidrechner.app.ui.theme.JetMono

@Composable
fun WikiScreen(onCalculate: (Peptide) -> Unit) {
    val statusBar = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    var expanded by remember { mutableStateOf<String?>(null) }

    Box(Modifier.fillMaxSize().background(Brand.Bg)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                        .background(Brand.hero)
                        .padding(top = statusBar + 28.dp, bottom = 24.dp, start = 20.dp, end = 20.dp),
                ) {
                    Column {
                        Text("Peptid-Wiki", style = MaterialTheme.typography.displaySmall, color = Color.White)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${PeptideRepository.peptides.size} Peptide · antippen für Details",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.72f),
                        )
                    }
                }
            }
            items(PeptideRepository.peptides, key = { it.name }) { p ->
                WikiCard(
                    peptide = p,
                    expanded = expanded == p.name,
                    onToggle = { expanded = if (expanded == p.name) null else p.name },
                    onCalculate = { onCalculate(p) },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun WikiCard(
    peptide: Peptide,
    expanded: Boolean,
    onToggle: () -> Unit,
    onCalculate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onToggle)
            .animateContentSize(),
        radius = 20,
        padding = PaddingValues(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(peptide.name, style = MaterialTheme.typography.titleMedium, color = Brand.TextStrong)
                peptide.aka?.let {
                    Text(it, style = MaterialTheme.typography.bodyMedium, color = Brand.TextLight)
                }
            }
            Icon(
                if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Brand.Primary,
            )
        }

        if (expanded) {
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Pill("${peptide.category.emoji} ${peptide.category.title}")
                if (peptide.nasal) Pill("🫧 Nasenspray")
            }
            Spacer(Modifier.height(10.dp))
            InfoLine("Typischer Bereich", peptide.doseRange)
            InfoLine("Standard-Vial", "${peptide.defaultVialMg.fmt0()} mg")
            InfoLine("Empf. Wasser", "${peptide.defaultWaterMl.fmt0()} ml")
            peptide.note?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, style = MaterialTheme.typography.bodyMedium, color = Brand.TextLight)
            }
            Spacer(Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brand.primaryToAccent)
                    .clickable(onClick = onCalculate)
                    .padding(vertical = 13.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("Im Rechner berechnen", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Brand.TextMuted)
        Text(value, fontFamily = JetMono, fontSize = 13.sp, color = Brand.TextStrong, fontWeight = FontWeight.Bold)
    }
}

private fun Double.fmt0(): String {
    val l = toLong()
    return if (this == l.toDouble()) l.toString() else toString()
}
