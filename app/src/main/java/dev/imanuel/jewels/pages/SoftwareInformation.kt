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
import dev.imanuel.jewels.utils.Device

@Composable
fun SoftwareInformation(device: Device) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState()).weight(1f, fill = false)) {
            if (device.os != null) {
                ElevatedCard(modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Betriebssystem", style = MaterialTheme.typography.headlineMedium)
                        Text("${device.os.name} ${device.os.version}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            if (device.kernel != null) {
                ElevatedCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Kernel", style = MaterialTheme.typography.headlineMedium)
                        Text("Version", style = MaterialTheme.typography.labelMedium)
                        Text(device.kernel.version, style = MaterialTheme.typography.bodyMedium)

                        Text("Architektur", style = MaterialTheme.typography.labelMedium)
                        Text(device.kernel.architecture, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}