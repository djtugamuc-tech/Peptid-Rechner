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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.peptidrechner.app.data.tracking.InjectionEntry
import de.peptidrechner.app.data.tracking.TrackingRepository
import de.peptidrechner.app.ui.components.GlassCard
import de.peptidrechner.app.ui.fmt
import de.peptidrechner.app.ui.theme.Brand
import de.peptidrechner.app.ui.theme.JetMono
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun TrackerScreen() {
    val entries by TrackingRepository.entries.collectAsState()
    val statusBar = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    val now = System.currentTimeMillis()
    val weekAgo = now - 7L * 24 * 60 * 60 * 1000
    val thisWeek = entries.count { it.timestamp >= weekAgo }
    val grouped = entries.groupBy { dayKey(it.timestamp) }

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
                        .padding(top = statusBar + 28.dp, bottom = 22.dp, start = 20.dp, end = 20.dp),
                ) {
                    Column {
                        Text("Mein Tracker", style = MaterialTheme.typography.displaySmall, color = Color.White)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Protokollierte Injektionen",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.72f),
                        )
                        Spacer(Modifier.height(18.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Stat("Diese Woche", "$thisWeek", Modifier.weight(1f))
                            Stat("Gesamt", "${entries.size}", Modifier.weight(1f))
                        }
                    }
                }
            }

            item {
                de.peptidrechner.app.ui.components.ReminderCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )
            }

            if (entries.isEmpty()) {
                item {
                    Column(
                        Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("💉", fontSize = 40.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Noch keine Einträge",
                            style = MaterialTheme.typography.titleMedium,
                            color = Brand.TextStrong,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Tippe im Rechner auf »Injektion protokollieren«, um deinen Verlauf hier zu sehen.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Brand.TextLight,
                        )
                    }
                }
            }

            grouped.forEach { (day, dayEntries) ->
                item(key = "h_$day") {
                    Text(
                        day.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = Brand.TextMuted,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 18.dp, top = 12.dp, bottom = 2.dp),
                    )
                }
                dayEntries.forEach { entry ->
                    item(key = entry.id) {
                        EntryRow(entry, Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun Stat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.10f))
            .padding(14.dp),
    ) {
        Text(value, fontFamily = JetMono, fontSize = 26.sp, color = Color.White, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.7f))
    }
}

@Composable
private fun EntryRow(entry: InjectionEntry, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier.fillMaxWidth(), radius = 18, padding = PaddingValues(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(Brand.primaryToAccent),
                contentAlignment = Alignment.Center,
            ) {
                Text(entry.peptideName.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(entry.peptideName, style = MaterialTheme.typography.titleMedium, color = Brand.TextStrong)
                Text(
                    "${entry.doseText} · ${entry.units.fmt(1)} IE · ${timeOf(entry.timestamp)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = JetMono,
                    color = Brand.TextLight,
                )
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(50))
                    .clickable { TrackingRepository.delete(entry.id) },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Löschen", tint = Brand.TextMuted, modifier = Modifier.size(20.dp))
            }
        }
    }
}

private fun dayKey(ts: Long): String {
    val cal = Calendar.getInstance().apply { timeInMillis = ts }
    val today = Calendar.getInstance()
    val yest = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
    fun sameDay(a: Calendar, b: Calendar) =
        a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
            a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
    return when {
        sameDay(cal, today) -> "Heute"
        sameDay(cal, yest) -> "Gestern"
        else -> SimpleDateFormat("EEEE, d. MMMM yyyy", Locale.GERMANY).format(Date(ts))
    }
}

private fun timeOf(ts: Long): String =
    SimpleDateFormat("HH:mm", Locale.GERMANY).format(Date(ts))
