package dev.imanuel.jewels.pages

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import dev.imanuel.jewels.detection.ServerSettings
import dev.imanuel.jewels.detection.deleteSettings
import kotlinx.serialization.json.Json
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(
    context: Context = koinInject(),
    serverSettings: ServerSettings = koinInject(),
    goToSetup: () -> Unit,
    goToJewels: () -> Unit
) {
    val dataClient by lazy { Wearable.getDataClient(context) }

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
                    OutlinedButton(onClick = {
                        deleteSettings(context)
                        goToSetup()
                    }, modifier = Modifier.padding(end = 16.dp)) {
                        Text("Zurück zum Scanner")
                    }
                    Button(onClick = {
                        val request = PutDataRequest.create("/settings").setUrgent().apply {
                            data = Json.encodeToString(ServerSettings.serializer(), serverSettings).toByteArray()
                        }
                        dataClient.putDataItem(request)
                        goToJewels()
                    }) {
                        Text("Anmelden")
                    }
                }
            }
        }
    }
}
