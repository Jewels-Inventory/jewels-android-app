package dev.imanuel.jewels

import android.content.Context
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import dev.imanuel.jewels.information.ApiService
import dev.imanuel.jewels.information.InformationCollector
import dev.imanuel.jewels.information.PushException
import dev.imanuel.jewels.utils.ServerSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SendDataWorker(
    private val client: ApiService,
    private val settings: ServerSettings,
    private val informationCollector: InformationCollector,
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    private suspend fun sendPhoneData() {
        withContext(Dispatchers.IO) {
            val device = informationCollector.collect()

            try {
                client.sendPhoneData("Bearer ${settings.token}", device)
            } catch (e: PushException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Die Daten konnten nicht gespeichert werden", Toast.LENGTH_LONG)
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

    private suspend fun sendWatchData() {
        withContext(Dispatchers.IO) {
            val device = informationCollector.collect()

            try {
                client.sendWatchData("Bearer ${settings.token}", device)
            } catch (e: PushException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Die Daten konnten nicht gespeichert werden", Toast.LENGTH_LONG)
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
            this.inputData.getString("type")?.let { type ->
                if (type == "phone") {
                    sendPhoneData()
                } else if (type == "watch") {
                    sendWatchData()
                }
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.failure(Data.Builder().putString("EXCEPTION", e.message).build())
        }
    }
}