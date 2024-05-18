package dev.imanuel.jewels.pages

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import dev.imanuel.jewels.information.Device
import org.koin.compose.koinInject

@Composable
fun getDeviceType(context: Context): String {
    return if (currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT) {
        "Tablet"
    } else {
        "Smartphone"
    }
}

@Composable
fun HardwareInformation(device: Device = koinInject()) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState()).weight(1f, fill = false)) {
            ElevatedCard(modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Über das ${getDeviceType(LocalContext.current)}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    if (device.hostname != null) {
                        Text("Name", style = MaterialTheme.typography.labelMedium)
                        Text(device.hostname, style = MaterialTheme.typography.bodyMedium)
                    }

                    Text("Hersteller", style = MaterialTheme.typography.labelMedium)
                    Text(device.manufacturer, style = MaterialTheme.typography.bodyMedium)

                    Text("Modell", style = MaterialTheme.typography.labelMedium)
                    Text(device.model, style = MaterialTheme.typography.bodyMedium)
                }
            }
            if (device.cpu != null) {
                ElevatedCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Prozessor", style = MaterialTheme.typography.headlineMedium)
                        Text("Hersteller", style = MaterialTheme.typography.labelMedium)
                        Text(device.cpu.manufacturer, style = MaterialTheme.typography.bodyMedium)

                        Text("Modell", style = MaterialTheme.typography.labelMedium)
                        Text(device.cpu.model, style = MaterialTheme.typography.bodyMedium)

                        Text("Anzahl Kerne", style = MaterialTheme.typography.labelMedium)
                        Text(device.cpu.cores.toString(), style = MaterialTheme.typography.bodyMedium)

                        Text("Geschwindigkeit", style = MaterialTheme.typography.labelMedium)
                        Text("%.2f GHz".format(device.cpu.speed), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            ElevatedCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Arbeitsspeicher", style = MaterialTheme.typography.headlineMedium)
                    Text("Größe", style = MaterialTheme.typography.labelMedium)
                    Text("%.2f GB".format(device.ram), style = MaterialTheme.typography.bodyMedium)
                }
            }
            if (device.mainboard != null) {
                ElevatedCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Mainboard", style = MaterialTheme.typography.headlineMedium)
                        Text("Hersteller", style = MaterialTheme.typography.labelMedium)
                        Text(device.mainboard.manufacturer, style = MaterialTheme.typography.bodyMedium)

                        Text("Modell", style = MaterialTheme.typography.labelMedium)
                        Text(device.mainboard.model, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
