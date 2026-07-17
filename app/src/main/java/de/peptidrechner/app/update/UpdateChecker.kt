package de.peptidrechner.app.update

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

data class UpdateInfo(
    val version: String,        // z. B. "v1.1"
    val downloadUrl: String,    // APK-URL
    val notes: String,
    val isNewer: Boolean,
)

object UpdateChecker {

    private const val LATEST_API =
        "https://api.github.com/repos/djtugamuc-tech/Peptid-Rechner/releases/latest"
    private const val APK_NAME = "peptid-rechner-update.apk"

    fun currentVersion(context: Context): String =
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: "0"

    /** Neueste Version von GitHub holen; null bei Fehler/keinem Release. */
    suspend fun fetchLatest(context: Context): UpdateInfo? = withContext(Dispatchers.IO) {
        runCatching {
            val conn = (URL(LATEST_API).openConnection() as HttpURLConnection).apply {
                connectTimeout = 8000
                readTimeout = 8000
                setRequestProperty("Accept", "application/vnd.github+json")
                setRequestProperty("User-Agent", "Peptid-Rechner-App")
            }
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            val o = JSONObject(body)
            val tag = o.getString("tag_name")
            val notes = o.optString("body", "")
            var apkUrl = ""
            val assets = o.optJSONArray("assets")
            if (assets != null) {
                for (i in 0 until assets.length()) {
                    val a = assets.getJSONObject(i)
                    if (a.getString("name").endsWith(".apk", ignoreCase = true)) {
                        apkUrl = a.getString("browser_download_url")
                        break
                    }
                }
            }
            UpdateInfo(
                version = tag,
                downloadUrl = apkUrl,
                notes = notes,
                isNewer = isNewer(tag, currentVersion(context)),
            )
        }.getOrNull()
    }

    /** Vergleicht z. B. "v1.1" > "1.0" numerisch pro Segment. */
    fun isNewer(remoteTag: String, current: String): Boolean {
        fun parts(s: String) = s.trim().trimStart('v', 'V').split(".", "-")
            .mapNotNull { it.toIntOrNull() }
        val r = parts(remoteTag)
        val c = parts(current)
        for (i in 0 until maxOf(r.size, c.size)) {
            val rv = r.getOrElse(i) { 0 }
            val cv = c.getOrElse(i) { 0 }
            if (rv != cv) return rv > cv
        }
        return false
    }

    /**
     * Lädt die APK herunter und startet nach Abschluss die Installation.
     * Fordert bei Bedarf die Berechtigung „Unbekannte Apps installieren" an.
     */
    fun downloadAndInstall(context: Context, url: String) {
        if (url.isBlank()) {
            Toast.makeText(context, "Keine APK im Release gefunden.", Toast.LENGTH_LONG).show()
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            !context.packageManager.canRequestPackageInstalls()
        ) {
            Toast.makeText(
                context,
                "Bitte »Unbekannte Apps installieren« für Peptid-Rechner erlauben und erneut tippen.",
                Toast.LENGTH_LONG,
            ).show()
            context.startActivity(
                Intent(
                    Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse("package:${context.packageName}"),
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
            return
        }

        val target = File(context.getExternalFilesDir(null), APK_NAME)
        if (target.exists()) target.delete()

        val request = DownloadManager.Request(Uri.parse(url)).apply {
            setTitle("Peptid-Rechner Update")
            setDescription("Lädt die neue Version …")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalFilesDir(context, null, APK_NAME)
            setMimeType("application/vnd.android.package-archive")
        }
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val id = dm.enqueue(request)
        Toast.makeText(context, "Download gestartet …", Toast.LENGTH_SHORT).show()

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                val doneId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (doneId != id) return
                ctx.applicationContext.unregisterReceiver(this)
                install(ctx, target)
            }
        }
        ContextCompat.registerReceiver(
            context.applicationContext,
            receiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            ContextCompat.RECEIVER_EXPORTED,
        )
    }

    private fun install(context: Context, apk: File) {
        if (!apk.exists()) {
            Toast.makeText(context, "Download fehlgeschlagen.", Toast.LENGTH_LONG).show()
            return
        }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", apk)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
