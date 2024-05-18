package dev.imanuel.jewels.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.imanuel.jewels.R
import dev.imanuel.jewels.utils.collectPhoneOrTabletDeviceInformation
import dev.imanuel.jewels.utils.loadSettings

enum class SelectedTab {
    Jewels,
    Hardware,
    Storage,
    Software
}

fun sendData() {}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Information(goToSetup: () -> Unit) {
    val device = collectPhoneOrTabletDeviceInformation(LocalContext.current)
    val serverSettings = loadSettings(LocalContext.current)
    var selectedItem by remember { mutableStateOf(SelectedTab.Jewels) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Deine Hardware")
                },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                actions = {
                    IconButton(onClick = goToSetup) {
                        Icon(ImageVector.vectorResource(R.drawable.ic_logout), "Gerät entfernen")
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets(16.dp, 16.dp, 16.dp, 16.dp),
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                { Text("Infos hochladen") },
                { Icon(ImageVector.vectorResource(R.drawable.ic_upload), "Infos hochladen") },
                { sendData() }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = {
                        Icon(
                            ImageVector.vectorResource(R.drawable.ic_jewels),
                            contentDescription = "Jewels Server",
                        )
                    },
                    label = { Text("Jewels Server") },
                    selected = selectedItem == SelectedTab.Jewels,
                    onClick = { selectedItem = SelectedTab.Jewels }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            ImageVector.vectorResource(R.drawable.ic_hardware),
                            contentDescription = "Hardware"
                        )
                    },
                    label = { Text("Hardware") },
                    selected = selectedItem == SelectedTab.Hardware,
                    onClick = { selectedItem = SelectedTab.Hardware }
                )
                NavigationBarItem(
                    icon = { Icon(ImageVector.vectorResource(R.drawable.ic_storage), contentDescription = "Speicher") },
                    label = { Text("Speicher") },
                    selected = selectedItem == SelectedTab.Storage,
                    onClick = { selectedItem = SelectedTab.Storage }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            ImageVector.vectorResource(R.drawable.ic_software),
                            contentDescription = "Software"
                        )
                    },
                    label = { Text("Software") },
                    selected = selectedItem == SelectedTab.Software,
                    onClick = { selectedItem = SelectedTab.Software }
                )
            }
        }
    ) { innerPadding ->
        when (selectedItem) {
            SelectedTab.Jewels -> Column(modifier = Modifier.padding(innerPadding)) {
                Text("Verbunden", style = MaterialTheme.typography.displayMedium)
                Text(
                    "Du bist mit ${serverSettings?.host?.replace("https://", "")} verbunden",
                )
            }

            SelectedTab.Hardware -> Column(modifier = Modifier.padding(innerPadding)) {
                HardwareInformation(device)
            }

            SelectedTab.Storage -> Column(modifier = Modifier.padding(innerPadding)) {
                StorageInformation(device)
            }

            SelectedTab.Software -> Column(modifier = Modifier.padding(innerPadding)) {
                SoftwareInformation(device)
            }
        }
    }
}

@Preview
@Composable
fun InformationPreview() {
    Information { }
}
