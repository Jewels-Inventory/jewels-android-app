package dev.imanuel.jewels.pages

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import dev.imanuel.jewels.detection.ServerSettings
import dev.imanuel.jewels.detection.saveSettings
import dev.imanuel.jewels.utils.rememberJsonQrScanner
import dev.imanuel.jewels.utils.rememberQrScanner

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ServerSetup(
    goToLogin: () -> Unit
) {
    val context = LocalContext.current
    val startScan = rememberJsonQrScanner<ServerSettings>(
        onResult = { settings ->
            saveSettings(settings, context)
            goToLogin()
        },
        onCancel = { },
        onError = {
            Toast.makeText(context, "QR Code konnte nicht gelesen werden", Toast.LENGTH_SHORT).show()
        },
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Anmelden")
                },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Tipp einfach auf \"QR Code scannen\" und dann kannst du den Code in deinem Browser scannen.",
                    modifier = Modifier.padding(bottom = 16.dp, top = 16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                Button(
                    onClick = { startScan() },
                    modifier = Modifier.padding(top = 24.dp)
                ) {
                    Text("QR Code scannen")
                }
            }
        }
    }
}
