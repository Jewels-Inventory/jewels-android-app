package dev.imanuel.jewels

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            val request = OneTimeWorkRequestBuilder<SendDataWorker>().setInputData(
                Data.Builder().putString("type", "phone").build()
            )
                .build()

            WorkManager.getInstance(context).enqueue(request)
        }
    }
}
