package dev.imanuel.jewels.pages

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.imanuel.jewels.NavigationPage
import dev.imanuel.jewels.shared.ServerSettings
import dev.imanuel.jewels.shared.saveSettings
import dev.imanuel.jewels.utils.rememberJsonQrScanner

@Composable
fun ServerSetup(
    navController: NavController
) {
    val context = LocalContext.current
    val startScan = rememberJsonQrScanner<ServerSettings>(
        onResult = { settings ->
            context.saveSettings(settings)
            navController.navigate(NavigationPage.Login.name)
        },
        onCancel = { },
        onError = {
            Toast.makeText(context, "QR Code konnte nicht gelesen werden", Toast.LENGTH_SHORT)
                .show()
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
