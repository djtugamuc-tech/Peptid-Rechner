package de.peptidrechner.app.data.tracking

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

/**
 * Lokaler Speicher für protokollierte Injektionen.
 * Persistiert als JSON in SharedPreferences – kein Server, keine Konten.
 */
object TrackingRepository {

    private const val PREFS = "peptid_tracking"
    private const val KEY = "entries_v1"

    private var prefs: SharedPreferences? = null

    private val _entries = MutableStateFlow<List<InjectionEntry>>(emptyList())
    val entries: StateFlow<List<InjectionEntry>> = _entries.asStateFlow()

    fun init(context: Context) {
        if (prefs != null) return
        prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        _entries.value = load()
    }

    fun add(peptideName: String, doseText: String, units: Double, ml: Double, timestamp: Long) {
        val entry = InjectionEntry(
            id = timestamp,
            peptideName = peptideName,
            doseText = doseText,
            units = units,
            ml = ml,
            timestamp = timestamp,
        )
        val updated = (_entries.value + entry).sortedByDescending { it.timestamp }
        _entries.value = updated
        save(updated)
    }

    fun delete(id: Long) {
        val updated = _entries.value.filterNot { it.id == id }
        _entries.value = updated
        save(updated)
    }

    fun clear() {
        _entries.value = emptyList()
        save(emptyList())
    }

    private fun load(): List<InjectionEntry> {
        val raw = prefs?.getString(KEY, null) ?: return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    add(
                        InjectionEntry(
                            id = o.getLong("id"),
                            peptideName = o.getString("peptideName"),
                            doseText = o.getString("doseText"),
                            units = o.getDouble("units"),
                            ml = o.getDouble("ml"),
                            timestamp = o.getLong("timestamp"),
                        )
                    )
                }
            }.sortedByDescending { it.timestamp }
        }.getOrDefault(emptyList())
    }

    private fun save(list: List<InjectionEntry>) {
        val arr = JSONArray()
        list.forEach { e ->
            arr.put(
                JSONObject()
                    .put("id", e.id)
                    .put("peptideName", e.peptideName)
                    .put("doseText", e.doseText)
                    .put("units", e.units)
                    .put("ml", e.ml)
                    .put("timestamp", e.timestamp)
            )
        }
        // commit() (synchron) statt apply(): garantiert persistiert, auch wenn der
        // Prozess kurz danach beendet wird.
        prefs?.edit()?.putString(KEY, arr.toString())?.commit()
    }
}
