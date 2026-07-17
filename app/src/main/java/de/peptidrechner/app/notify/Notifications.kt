package de.peptidrechner.app.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import de.peptidrechner.app.MainActivity
import de.peptidrechner.app.R
import de.peptidrechner.app.data.tracking.TrackingRepository

object Notifications {

    const val CHANNEL_ID = "injection_reminders"
    private const val NOTIFICATION_ID = 4711

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Injektions-Erinnerungen",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Erinnert dich an deine nächste Injektion"
            }
            val mgr = context.getSystemService(NotificationManager::class.java)
            mgr.createNotificationChannel(channel)
        }
    }

    fun showReminder(context: Context) {
        ensureChannel(context)

        val last = TrackingRepository.entries.value.firstOrNull()
        val text = if (last != null) {
            "Zeit für deine nächste Injektion (zuletzt: ${last.peptideName})."
        } else {
            "Zeit für deine nächste Injektion."
        }

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pending = PendingIntent.getActivity(
            context,
            0,
            openIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_reminder)
            .setContentTitle("Peptid-Rechner")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pending)
            .build()

        // Ab Android 13 darf ohne POST_NOTIFICATIONS nicht gepostet werden.
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            try {
                NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
            } catch (_: SecurityException) {
                // Berechtigung fehlt – still ignorieren.
            }
        }
    }
}
