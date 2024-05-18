@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package dev.imanuel.jewels.pages

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dev.imanuel.jewels.R
import dev.imanuel.jewels.SendDataWorker
import dev.imanuel.jewels.information.Device
import dev.imanuel.jewels.information.InformationCollector
import dev.imanuel.jewels.utils.ServerSettings
import dev.imanuel.jewels.utils.deleteSettings
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

enum class SelectedTab {
    Jewels, Hardware, Storage, Software
}

@Composable
fun AppBar(
    device: Device?,
    context: Context = koinInject(),
    goToSetup: () -> Unit
) {
    TopAppBar(
        title = {
            Text("Dein ${device?.model ?: getDeviceType()}")
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        actions = {
            IconButton(
                onClick = {
                    deleteSettings(context)
                    goToSetup()
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(ImageVector.vectorResource(R.drawable.ic_logout), "Gerät entfernen")
            }
        })
}

@Composable
fun Tabs(state: PagerState, device: Device?) {
    val coroutineScope = rememberCoroutineScope()

    TabRow(
        selectedTabIndex = state.currentPage,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.primary,
    ) {
        Tab(
            icon = {
                Icon(
                    ImageVector.vectorResource(R.drawable.ic_jewels), contentDescription = "Jewels"
                )
            },
            text = { Text("Jewels", maxLines = 1, overflow = TextOverflow.Ellipsis) },
            selected = state.currentPage == SelectedTab.Jewels.ordinal,
            onClick = {
                coroutineScope.launch {
                    state.animateScrollToPage(SelectedTab.Jewels.ordinal)
                }
            },
        )
        Tab(
            icon = {
                Icon(
                    ImageVector.vectorResource(R.drawable.ic_hardware), contentDescription = "Hardware"
                )
            },
            text = { Text("Hardware", maxLines = 1, overflow = TextOverflow.Ellipsis) },
            selected = state.currentPage == SelectedTab.Hardware.ordinal,
            onClick = {
                coroutineScope.launch {
                    state.animateScrollToPage(SelectedTab.Hardware.ordinal)
                }
            },
            enabled = device != null,
        )
        Tab(
            icon = {
                Icon(
                    ImageVector.vectorResource(R.drawable.ic_storage), contentDescription = "Speicher"
                )
            },
            text = { Text("Speicher", maxLines = 1, overflow = TextOverflow.Ellipsis) },
            selected = state.currentPage == SelectedTab.Storage.ordinal,
            onClick = {
                coroutineScope.launch {
                    state.animateScrollToPage(SelectedTab.Storage.ordinal)
                }
            },
            enabled = device != null,
        )
        Tab(
            icon = {
                Icon(
                    ImageVector.vectorResource(R.drawable.ic_software), contentDescription = "Software"
                )
            },
            text = { Text("Software", maxLines = 1, overflow = TextOverflow.Ellipsis) },
            selected = state.currentPage == SelectedTab.Software.ordinal,
            onClick = {
                coroutineScope.launch {
                    state.animateScrollToPage(SelectedTab.Software.ordinal)
                }
            },
            enabled = device != null,
        )
    }
}

@Composable
fun Information(
    context: Context = koinInject(),
    collector: InformationCollector = koinInject(),
    serverSettings: ServerSettings = koinInject(),
    goToSetup: () -> Unit
) {
    var device by remember { mutableStateOf<Device?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val tabState = rememberPagerState(initialPage = SelectedTab.Jewels.ordinal) {
        SelectedTab.entries.size
    }

    LaunchedEffect(device) {
        if (device == null) {
            coroutineScope.launch {
                device = collector.collect()
            }
        }
    }

    Scaffold(
        topBar = {
            Column {
                AppBar(device, goToSetup = goToSetup)
                Tabs(tabState, device)
            }
        },
        contentWindowInsets = WindowInsets(16.dp, 16.dp, 16.dp, 16.dp),
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            ExtendedFloatingActionButton({ Text("Infos hochladen") },
                { Icon(ImageVector.vectorResource(R.drawable.ic_upload), "Infos hochladen") },
                {
                    val request = OneTimeWorkRequestBuilder<SendDataWorker>().setInputData(
                        Data.Builder().putString("type", "phone").build()
                    ).build()
                    WorkManager.getInstance(context).enqueue(request)
                    Toast.makeText(context, "Daten werden hochgeladen", Toast.LENGTH_LONG)
                })
        }) { innerPadding ->
        HorizontalPager(state = tabState, modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (SelectedTab.entries[it]) {
                SelectedTab.Jewels -> Column(modifier = Modifier.padding(top = 16.dp).fillMaxSize()) {
                    Text("Verbunden", style = MaterialTheme.typography.displayMedium)
                    Text(
                        "Du bist mit ${serverSettings.host.replace("https://", "")} verbunden",
                    )
                }

                SelectedTab.Hardware -> Column(modifier = Modifier.fillMaxSize()) {
                    HardwareInformation(device!!)
                }

                SelectedTab.Storage -> Column(modifier = Modifier.fillMaxSize()) {
                    StorageInformation(device!!)
                }

                SelectedTab.Software -> Column(modifier = Modifier.fillMaxSize()) {
                    SoftwareInformation(device!!)
                }
            }
        }
    }
}

@Preview
@Composable
fun InformationPreview() {
    Information {}
}
