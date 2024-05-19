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

@Composable
fun StorageInformation(device: dev.imanuel.jewels.detection.information.Device) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState()).weight(1f, fill = false)) {
            device.drives?.forEach { drive ->
                ElevatedCard(modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(drive.name, style = MaterialTheme.typography.headlineMedium)
                        Text("Größe", style = MaterialTheme.typography.labelMedium)
                        Text("%.2f GB".format(drive.size), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}