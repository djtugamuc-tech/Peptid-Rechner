package de.peptidrechner.app.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import de.peptidrechner.app.data.reminder.ReminderConfig
import de.peptidrechner.app.data.reminder.ReminderStore
import de.peptidrechner.app.notify.ReminderScheduler
import de.peptidrechner.app.ui.theme.Brand
import de.peptidrechner.app.ui.theme.JetMono
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReminderCard(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val config by ReminderStore.config.collectAsState()
    var showTimePicker by remember { mutableStateOf(false) }

    fun persist(newConfig: ReminderConfig) {
        ReminderStore.update(newConfig)
        ReminderScheduler.apply(context, newConfig)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        // Auch ohne Berechtigung planen wir; die Notification wird dann nur
        // nicht angezeigt. Aber sinnvoll: nur aktivieren, wenn erlaubt.
        persist(config.copy(enabled = granted))
    }

    fun requestEnable() {
        val needsPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        if (needsPermission) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            persist(config.copy(enabled = true))
        }
    }

    GlassCard(modifier = modifier.fillMaxWidth(), radius = 20, padding = PaddingValues(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brand.Primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Notifications, null, tint = Brand.Primary, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Erinnerung", style = MaterialTheme.typography.titleMedium, color = Brand.TextStrong, fontWeight = FontWeight.Bold)
                Text(
                    if (config.enabled) "${config.intervalLabel} · ${config.timeLabel}" else "Aus",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Brand.TextLight,
                )
            }
            Switch(
                checked = config.enabled,
                onCheckedChange = { on -> if (on) requestEnable() else persist(config.copy(enabled = false)) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Brand.Primary,
                ),
            )
        }

        AnimatedVisibility(visible = config.enabled) {
            Column {
                Spacer(Modifier.height(14.dp))
                // Uhrzeit
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brand.CardBgAlt)
                        .clickable { showTimePicker = true }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Uhrzeit", style = MaterialTheme.typography.bodyLarge, color = Brand.TextStrong)
                    Text(config.timeLabel, fontFamily = JetMono, fontSize = 16.sp, color = Brand.Primary, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(12.dp))
                Text("Häufigkeit", style = MaterialTheme.typography.labelMedium, color = Brand.TextMuted, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                IntervalSelector(config.intervalDays) { persist(config.copy(intervalDays = it)) }

                Spacer(Modifier.height(12.dp))
                Text(
                    "Nächste Erinnerung: ${formatNext(ReminderScheduler.nextTrigger(config, System.currentTimeMillis()))}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Brand.TextLight,
                )
            }
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            initialHour = config.hour,
            initialMinute = config.minute,
            onConfirm = { h, m -> showTimePicker = false; persist(config.copy(hour = h, minute = m)) },
            onDismiss = { showTimePicker = false },
        )
    }
}

@Composable
private fun IntervalSelector(selected: Int, onSelected: (Int) -> Unit) {
    val options = listOf(1 to "Täglich", 2 to "2 Tage", 3 to "3 Tage", 7 to "Wöchentl.")
    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        options.forEachIndexed { i, (days, label) ->
            SegmentedButton(
                selected = selected == days,
                onClick = { onSelected(days) },
                shape = SegmentedButtonDefaults.itemShape(i, options.size),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = Brand.Primary,
                    activeContentColor = Color.White,
                ),
            ) { Text(label, fontSize = 11.sp) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val state = rememberTimePickerState(initialHour = initialHour, initialMinute = initialMinute, is24Hour = true)
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = { onConfirm(state.hour, state.minute) }) { Text("OK") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Abbrechen") } },
        title = { Text("Erinnerungszeit") },
        text = {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TimePicker(state = state)
            }
        },
    )
}

private fun formatNext(ts: Long): String =
    SimpleDateFormat("EEE, d. MMM · HH:mm", Locale.GERMANY).format(Date(ts))
