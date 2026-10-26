package dev.imanuel.jewels

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import dev.imanuel.jewels.shared.ServerSettings
import dev.imanuel.jewels.shared.deleteSettings
import dev.imanuel.jewels.shared.information.Device
import dev.imanuel.jewels.shared.information.DeviceType
import dev.imanuel.jewels.shared.information.InformationCollector
import dev.imanuel.jewels.shared.saveSettings
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
                            settings = Json.decodeFromString(
                                ServerSettings.serializer(),
                                data.decodeToString()
                            )
                            context.saveSettings(settings!!)
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
                                        context.saveSettings(settings!!)
                                    } else {
                                        settings = null
                                        context.deleteSettings()
                                    }
                                } else if (event.type == DataEvent.TYPE_DELETED) {
                                    context.deleteSettings()
                                    settings = null
                                }
                            }
                    }
                }
        }
    }

    JewelsTheme {
        AppScaffold {
            if (settings == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Bitte melde dich am Telefon an",
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                PageOtpScreen()
            }
        }
    }
}
