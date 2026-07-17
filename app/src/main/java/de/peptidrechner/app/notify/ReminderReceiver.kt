package de.peptidrechner.app.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.peptidrechner.app.data.reminder.ReminderStore
import de.peptidrechner.app.data.tracking.TrackingRepository

/**
 * Wird zur eingestellten Zeit ausgelöst: zeigt die Benachrichtigung und plant
 * die nächste Erinnerung im gewählten Intervall.
 */
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != ACTION_FIRE) return

        // Beide Stores initialisieren – der Receiver kann in einem frischen
        // Prozess laufen (z. B. nach Doze), in dem noch nichts geladen wurde.
        ReminderStore.init(context)
        TrackingRepository.init(context)
        val config = ReminderStore.current()
        if (!config.enabled) return

        Notifications.showReminder(context)
        // Nächsten Termin einplanen.
        ReminderScheduler.schedule(context, config)
    }

    companion object {
        const val ACTION_FIRE = "de.peptidrechner.app.REMINDER_FIRE"
    }
}
