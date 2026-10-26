package dev.imanuel.jewels

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.content.edit
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.imanuel.jewels.shared.api.ApiClient

class EolCheckWorker(
    private val apiClient: ApiClient,
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        checkEolDevices()

        return Result.success()
    }

    private fun showNotification(deviceId: Int, title: String, message: String) {
        val notification = Notification.Builder(applicationContext, "device-eol")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_jewels)
            .build()
        val notificationManager =
            applicationContext.getSystemService(NotificationManager::class.java)
        try {
            notificationManager.notify(deviceId, notification)
        } catch (ex: Exception) {
            Log.e("showNotification", ex.message!!)
        }
    }

    private fun markDeviceNotified(deviceId: String) {
        applicationContext
            .getSharedPreferences("device-notifications", Context.MODE_PRIVATE)
            .edit {
                putBoolean(deviceId, true)
            }
    }

    private fun notifiedBefore(deviceId: String): Boolean {
        return applicationContext
            .getSharedPreferences("device-notifications", Context.MODE_PRIVATE)
            .getBoolean(deviceId, false)
    }

    private suspend fun checkEolDevices() {
        val eolDevices = apiClient.getEolDevices()
        for ((who, devices) in eolDevices) {
            for ((id, _, _, model, manufacturer) in devices) {
                if (notifiedBefore(id)) continue
                if (who == "me") {
                    showNotification(
                        id.hashCode(),
                        "Du brauchst bald was neues",
                        "Dein $manufacturer $model ist bald aus dem Support raus"
                    )
                } else {
                    showNotification(
                        id.hashCode(),
                        "$who braucht bald was neues",
                        "Das $manufacturer $model von $who ist bald aus dem Support raus"
                    )
                }
                markDeviceNotified(id)
            }
        }
    }
}