package dev.imanuel.jewels.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.imanuel.jewels.utils.deleteSettings
import dev.imanuel.jewels.utils.loadSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(goToSetup: () -> Unit, goToInformation: () -> Unit) {
    val context = LocalContext.current
    val serverSettings = loadSettings(context)

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Anmelden")
                },
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
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
                            append(serverSettings?.host?.replace("https://", ""))
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
                    Button(onClick = goToInformation) {
                        Text("Anmelden")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun LoginPreview() {
    Login({ }) { }
}
