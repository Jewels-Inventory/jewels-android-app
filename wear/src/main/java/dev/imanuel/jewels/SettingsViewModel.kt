package dev.imanuel.jewels

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import dev.imanuel.jewels.detection.ServerSettings
import dev.imanuel.jewels.detection.saveSettings
import kotlinx.serialization.json.Json

class SettingsViewModel(private val context: Context) : ViewModel(),
    DataClient.OnDataChangedListener {
    private var _settings: MutableState<ServerSettings?> = mutableStateOf(null)

    val settings: ServerSettings? = _settings.value

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents
            .filter { it.type == DataEvent.TYPE_CHANGED && it.dataItem.uri.path == "/settings" }
            .forEach { event ->
                val data = event.dataItem.data
                if (data != null) {
                    val sendSettings = Json.decodeFromString<ServerSettings>(data.decodeToString())
                    _settings.value = sendSettings
                    saveSettings(sendSettings, context)
                }
            }
    }
}
