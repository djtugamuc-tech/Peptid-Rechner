package de.peptidrechner.app.data.reminder

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Konfiguration der Injektions-Erinnerung.
 *
 * @param intervalDays 1 = täglich, 2/3 = alle 2/3 Tage, 7 = wöchentlich.
 */
data class ReminderConfig(
    val enabled: Boolean = false,
    val hour: Int = 9,
    val minute: Int = 0,
    val intervalDays: Int = 1,
) {
    val timeLabel: String
        get() = "%02d:%02d".format(hour, minute)

    val intervalLabel: String
        get() = when (intervalDays) {
            1 -> "Täglich"
            2 -> "Alle 2 Tage"
            3 -> "Alle 3 Tage"
            7 -> "Wöchentlich"
            else -> "Alle $intervalDays Tage"
        }
}

/** Lokaler Speicher der Erinnerungs-Einstellungen (SharedPreferences). */
object ReminderStore {

    private const val PREFS = "peptid_reminder"
    private var prefs: SharedPreferences? = null

    private val _config = MutableStateFlow(ReminderConfig())
    val config: StateFlow<ReminderConfig> = _config.asStateFlow()

    fun init(context: Context) {
        if (prefs != null) return
        prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        _config.value = load()
    }

    fun update(config: ReminderConfig) {
        _config.value = config
        prefs?.edit()
            ?.putBoolean("enabled", config.enabled)
            ?.putInt("hour", config.hour)
            ?.putInt("minute", config.minute)
            ?.putInt("intervalDays", config.intervalDays)
            ?.commit()
    }

    fun current(): ReminderConfig = _config.value

    private fun load(): ReminderConfig {
        val p = prefs ?: return ReminderConfig()
        return ReminderConfig(
            enabled = p.getBoolean("enabled", false),
            hour = p.getInt("hour", 9),
            minute = p.getInt("minute", 0),
            intervalDays = p.getInt("intervalDays", 1),
        )
    }
}
