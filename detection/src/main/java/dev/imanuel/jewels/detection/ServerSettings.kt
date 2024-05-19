package dev.imanuel.jewels.detection

import android.content.Context
import kotlinx.serialization.Serializable

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
    val pref = context.getSharedPreferences("server-settings", Context.MODE_PRIVATE).edit()
    pref.putString("token", settings.token)
    pref.putString("server", settings.host)
    pref.apply()
}

fun deleteSettings(context: Context) {
    val pref = context.getSharedPreferences("server-settings", Context.MODE_PRIVATE).edit()
    pref.remove("token")
    pref.remove("server")
    pref.apply()
}
