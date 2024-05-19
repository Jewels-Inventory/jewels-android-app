package dev.imanuel.jewels

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import dev.imanuel.jewels.detection.SendDataWorker
import dev.imanuel.jewels.detection.ServerSettings
import dev.imanuel.jewels.detection.deleteSettings
import dev.imanuel.jewels.detection.information.Device
import dev.imanuel.jewels.detection.information.DeviceType
import dev.imanuel.jewels.detection.information.InformationCollector
import dev.imanuel.jewels.detection.saveSettings
import dev.imanuel.jewels.theme.JewelsTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp(context: Context = koinInject(), collector: InformationCollector = koinInject()) {
    val coroutineScope = rememberCoroutineScope()
    val dataClient by lazy { Wearable.getDataClient(context) }
    var settings by remember { mutableStateOf<ServerSettings?>(null) }
    var deviceInformation by remember { mutableStateOf<Device?>(null) }

    LaunchedEffect(deviceInformation) {
        coroutineScope.launch {
            val device = collector.collect(DeviceType.Watch)
            val request = PutDataRequest.create("/wear-device").setUrgent().apply {
                data = Json.encodeToString(Device.serializer(), device).toByteArray()
            }
            dataClient.putDataItem(request)
            deviceInformation = device
        }
    }
    LaunchedEffect(settings) {
        coroutineScope.launch {
            dataClient
                .dataItems
                .await()
                .firstOrNull { it.uri.path == "/settings" }
                .let { item ->
                    if (item != null) {
                        val data = item.data
                        if (data != null) {
                            settings = Json.decodeFromString(ServerSettings.serializer(), data.decodeToString())
                        }
                    }

                    dataClient.addListener { events ->
                        events
                            .filter { it.dataItem.uri.path == "/settings" }
                            .forEach { event ->
                                if (event.type == DataEvent.TYPE_CHANGED) {
                                    val data = event.dataItem.data
                                    if (data != null) {
                                        settings =
                                            Json.decodeFromString(
                                                ServerSettings.serializer(),
                                                data.decodeToString()
                                            )
                                        saveSettings(settings!!, context)
                                    } else {
                                        settings = null
                                        deleteSettings(context)
                                    }
                                } else if (event.type == DataEvent.TYPE_DELETED) {
                                    deleteSettings(context)
                                    settings = null
                                }
                            }
                    }
                }
        }
    }

    JewelsTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            if (settings == null) {
                Text("Bitte melde dich am Telefon an")
            } else if (deviceInformation != null) {
                Button(onClick = {
                    val request = OneTimeWorkRequestBuilder<SendDataWorker>().setInputData(
                        Data.Builder().putString("data", Json.encodeToString(Device.serializer(), deviceInformation!!))
                            .build()
                    ).build()
                    WorkManager.getInstance(context).enqueue(request)
                    Toast.makeText(context, "Daten werden hochgeladen", Toast.LENGTH_LONG).show()
                }) {
                    Text("Daten hochladen")
                }
            }
        }
    }
}
