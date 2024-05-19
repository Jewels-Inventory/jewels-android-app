package dev.imanuel.jewels.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.imanuel.jewels.detection.information.Device
import dev.imanuel.jewels.detection.information.DeviceType

@Composable
fun HardwareInformation(device: Device) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState()).weight(1f, fill = false)) {
            ElevatedCard(modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        if (device.type == DeviceType.Handheld) {
                            "Über das ${getHandheldType()}"
                        } else {
                            "Über die Smartwatch"
                        },
                        style = MaterialTheme.typography.headlineMedium
                    )
                    if (device.hostname != null) {
                        Text("Name", style = MaterialTheme.typography.labelMedium)
                        Text(device.hostname!!, style = MaterialTheme.typography.bodyMedium)
                    }

                    Text("Hersteller", style = MaterialTheme.typography.labelMedium)
                    Text(device.manufacturer, style = MaterialTheme.typography.bodyMedium)

                    Text("Modell", style = MaterialTheme.typography.labelMedium)
                    Text(device.model, style = MaterialTheme.typography.bodyMedium)
                }
            }
            if (device.cpu != null) {
                val cpu = device.cpu!!
                ElevatedCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Prozessor", style = MaterialTheme.typography.headlineMedium)
                        Text("Hersteller", style = MaterialTheme.typography.labelMedium)
                        Text(cpu.manufacturer, style = MaterialTheme.typography.bodyMedium)

                        Text("Modell", style = MaterialTheme.typography.labelMedium)
                        Text(cpu.model, style = MaterialTheme.typography.bodyMedium)

                        Text("Anzahl Kerne", style = MaterialTheme.typography.labelMedium)
                        Text(cpu.cores.toString(), style = MaterialTheme.typography.bodyMedium)

                        Text("Geschwindigkeit", style = MaterialTheme.typography.labelMedium)
                        Text("%.2f GHz".format(cpu.speed), style = MaterialTheme.typography.bodyMedium)
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
                val mainboard = device.mainboard!!
                ElevatedCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Mainboard", style = MaterialTheme.typography.headlineMedium)
                        Text("Hersteller", style = MaterialTheme.typography.labelMedium)
                        Text(mainboard.manufacturer, style = MaterialTheme.typography.bodyMedium)

                        Text("Modell", style = MaterialTheme.typography.labelMedium)
                        Text(mainboard.model, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
