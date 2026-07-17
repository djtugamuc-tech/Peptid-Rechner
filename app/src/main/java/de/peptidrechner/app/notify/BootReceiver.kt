package de.peptidrechner.app.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.peptidrechner.app.data.reminder.ReminderStore

/** Stellt die Erinnerung nach einem Geräteneustart wieder her. */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        ReminderStore.init(context)
        val config = ReminderStore.current()
        if (config.enabled) {
            ReminderScheduler.schedule(context, config)
        }
    }
}
