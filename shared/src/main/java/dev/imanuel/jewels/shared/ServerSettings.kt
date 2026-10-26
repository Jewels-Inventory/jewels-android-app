package dev.imanuel.jewels.shared

import android.content.Context
import androidx.core.content.edit
import kotlinx.serialization.Serializable

@Serializable
data class ServerSettings(val token: String, val host: String)

fun Context.loadSettings(): ServerSettings? {
    val pref = getSharedPreferences("server-settings", Context.MODE_PRIVATE)
    val token = pref.getString("token", null)
    val server = pref.getString("server", null)

    return if (token == null || server == null) {
        null
    } else {
        ServerSettings(token, server)
    }
}

fun Context.saveSettings(settings: ServerSettings) {
    getSharedPreferences("server-settings", Context.MODE_PRIVATE).edit {
        putString("token", settings.token)
        putString("server", settings.host)
    }
}

fun Context.deleteSettings() {
    getSharedPreferences("server-settings", Context.MODE_PRIVATE).edit {
        remove("token")
        remove("server")
    }
}
