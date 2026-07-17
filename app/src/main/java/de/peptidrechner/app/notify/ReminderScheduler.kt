package de.peptidrechner.app.notify

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import de.peptidrechner.app.data.reminder.ReminderConfig
import java.util.Calendar

object ReminderScheduler {

    private const val REQUEST_CODE = 90210

    /** Erinnerung entsprechend der Konfiguration ein-/ausschalten. */
    fun apply(context: Context, config: ReminderConfig) {
        if (config.enabled) schedule(context, config) else cancel(context)
    }

    fun schedule(context: Context, config: ReminderConfig) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        val triggerAt = nextTrigger(config, System.currentTimeMillis())
        val pending = pendingIntent(context)
        // Inexakt (setAndAllowWhileIdle) – benötigt keine Exact-Alarm-Berechtigung
        // und weckt das Gerät auch im Doze-Modus.
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        alarmManager.cancel(pendingIntent(context))
    }

    /** Nächsten Auslösezeitpunkt >= [now] zur eingestellten Uhrzeit im Intervall berechnen. */
    fun nextTrigger(config: ReminderConfig, now: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, config.hour)
            set(Calendar.MINUTE, config.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val step = config.intervalDays.coerceAtLeast(1)
        while (cal.timeInMillis <= now) {
            cal.add(Calendar.DAY_OF_YEAR, step)
        }
        return cal.timeInMillis
    }

    private fun pendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = ReminderReceiver.ACTION_FIRE
        }
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }
}
