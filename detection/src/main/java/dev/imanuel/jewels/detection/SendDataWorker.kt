package dev.imanuel.jewels.detection

import android.content.Context
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import dev.imanuel.jewels.detection.information.ApiClient
import dev.imanuel.jewels.detection.information.Device
import dev.imanuel.jewels.detection.information.DeviceType
import dev.imanuel.jewels.detection.information.InformationCollector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class SendDataWorker(
    private val client: ApiClient,
    private val informationCollector: InformationCollector,
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    private suspend fun sendHandheldData() {
        withContext(Dispatchers.IO) {
            val device = informationCollector.collect(DeviceType.Handheld)

            sendData(device)
        }
    }

    private suspend fun sendWatchData() {
        withContext(Dispatchers.IO) {
            val device = informationCollector.collect(DeviceType.Watch)

            sendData(device)
        }
    }

    private suspend fun sendData(device: Device) {
        withContext(Dispatchers.IO) {
            try {
                client.sendData(device)
            } catch (e: dev.imanuel.jewels.detection.information.PushException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        applicationContext,
                        "Die Daten konnten nicht gespeichert werden",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    override suspend fun doWork(): Result {
        try {
            this.inputData.getInt("type", -1).let { type ->
                if (type == DeviceType.Handheld.ordinal) {
                    sendHandheldData()
                } else if (type == DeviceType.Watch.ordinal) {
                    sendWatchData()
                }
            }
            this.inputData.getString("data")?.let {
                val device = Json.decodeFromString(Device.serializer(), it)
                sendData(device)
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.failure(Data.Builder().putString("EXCEPTION", e.message).build())
        }
    }
}