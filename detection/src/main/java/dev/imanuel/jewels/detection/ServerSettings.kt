package dev.imanuel.jewels.detection

import android.content.Context
import kotlinx.serialization.Serializable
import androidx.core.content.edit

@Serializable
data class ServerSettings(val token: String, val host: String) : java.io.Serializable

fun loadSettings(context: Context): ServerSettings? {
    val pref = context.getSharedPreferences("server-settings", Context.MODE_PRIVATE)
    val token = pref.getString("token", null)
    val server = pref.getString("server", null)

    return if (token == null || server == null) {
        null
    } else {
        ServerSettings(token, server)
    }
}

fun saveSettings(settings: ServerSettings, context: Context) {
    context.getSharedPreferences("server-settings", Context.MODE_PRIVATE).edit {
        putString("token", settings.token)
        putString("server", settings.host)
    }
}

fun deleteSettings(context: Context) {
    context.getSharedPreferences("server-settings", Context.MODE_PRIVATE).edit {
        remove("token")
        remove("server")
    }
}
