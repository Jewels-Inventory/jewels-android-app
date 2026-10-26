package dev.imanuel.jewels.pages

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import dev.imanuel.jewels.NavigationPage
import dev.imanuel.jewels.shared.ServerSettings
import dev.imanuel.jewels.shared.api.ApiClient
import dev.imanuel.jewels.shared.deleteSettings
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.compose.koinInject

@Composable
fun Login(
    navController: NavController,
    apiClient: ApiClient = koinInject(),
    context: Context = koinInject(),
    serverSettings: ServerSettings = koinInject(),
) {
    val dataClient by lazy { Wearable.getDataClient(context) }
    val coroutineScope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    val checkAndSaveLogin = {
        coroutineScope.launch {
            try {
                apiClient.getMyJewels()
                val request = PutDataRequest.create("/settings").setUrgent().apply {
                    data = Json
                        .encodeToString(ServerSettings.serializer(), serverSettings)
                        .toByteArray()
                }
                dataClient.putDataItem(request)
                navController.navigate(NavigationPage.Jewels.name)
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Der Login ist nicht gültig, hast du das Gerät gespeichert?")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Anmelden")
                },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    "Code gescannt",
                    style = MaterialTheme.typography.displaySmall,
                )
                Text(
                    buildAnnotatedString {
                        append("Du hast einen Code für ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(serverSettings.host.replace("https://", ""))
                        }
                        append(" gescannt.")
                    },
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    "Wenn das korrekt ist, dann speicher das Gerät bitte im Browser und klick auf anmelden",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = {
                            context.deleteSettings()
                            navController.navigate(NavigationPage.ServerSetup.name)
                        },
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text("Zurück zum Scanner")
                    }
                    Button(onClick = {
                        checkAndSaveLogin()
                    }) {
                        Text("Anmelden")
                    }
                }
            }
        }
    }
}