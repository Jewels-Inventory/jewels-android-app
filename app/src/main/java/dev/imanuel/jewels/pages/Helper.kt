package dev.imanuel.jewels.pages

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dev.imanuel.jewels.detection.SendDataWorker
import dev.imanuel.jewels.detection.information.Device
import dev.imanuel.jewels.detection.information.DeviceType
import kotlinx.serialization.json.Json

enum class HandheldType {
    Tablet,
    Smartphone;

    override fun toString(): String {
        return if (this == Tablet) {
            "Tablet"
        } else {
            "Smartphone"
        }
    }
}

@Composable
fun getHandheldType(): HandheldType {
    return if (currentWindowAdaptiveInfo().windowSizeClass.isWidthAtLeastBreakpoint(600)) {
        HandheldType.Tablet
    } else {
        HandheldType.Smartphone
    }
}

fun uploadData(handheldType: HandheldType, device: Device, context: Context) {
    val wearableRequest = OneTimeWorkRequestBuilder<SendDataWorker>().setInputData(
        Data.Builder().putString("data", Json.encodeToString(Device.serializer(), device))
            .build()
    ).build()
    WorkManager.getInstance(context).enqueue(wearableRequest)
    Toast.makeText(
        context, if (device.type == DeviceType.Handheld) {
            "Infos vom $handheldType ${device.model} werden hochgeladen"
        } else {
            "Infos von der Watch ${device.model} werden hochgeladen"
        }, Toast.LENGTH_LONG
    )
        .show()
}
