package dev.imanuel.jewels.detection.information

import android.content.Context
import android.util.Log
import dev.imanuel.jewels.detection.loadSettings
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.Serializable

@Serializable
data class PushData(val message: String?)

class PushException(override val message: String?) : Exception(message)

class ApiClient(private val context: Context, private val httpClient: HttpClient) {
    suspend fun sendData(device: Device) {
        val deviceType = if (device.type == DeviceType.Handheld) "phone" else "watch"
        val settings = loadSettings(context);
        if (settings != null) {
            try {
                httpClient.post("device/${deviceType}") {
                    setBody(device)
                }
            } catch (ex: Exception) {
                throw PushException(ex.message)
            }
        }
    }
}
