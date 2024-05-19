@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package dev.imanuel.jewels.pages

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.imanuel.jewels.R
import dev.imanuel.jewels.detection.information.Device
import dev.imanuel.jewels.detection.information.DeviceType
import dev.imanuel.jewels.pages.components.BottomNavBar
import dev.imanuel.jewels.pages.components.TopBarActions
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

enum class SelectedTab {
    Hardware,
    Storage,
    Software
}

@Composable
fun AppBar(
    selectedDevice: Device?,
    goToSetup: () -> Unit,
) {
    TopAppBar(
        title = {
            if (selectedDevice?.type == DeviceType.Watch) {
                Text("Deine ${selectedDevice.model}")
            } else {
                Text("Dein ${selectedDevice?.model ?: getHandheldType()}")
            }
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
        actions = {
            TopBarActions(goToSetup = goToSetup)
        })
}

@Composable
fun Tabs(state: PagerState, device: Device?) {
    val coroutineScope = rememberCoroutineScope()

    TabRow(
        selectedTabIndex = state.currentPage,
    ) {
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
    device: Device,
    navController: NavController,
    hasWatch: Boolean,
    isTablet: Boolean,
    goToSetup: () -> Unit
) {
    val tabState = rememberPagerState(initialPage = SelectedTab.Hardware.ordinal) {
        SelectedTab.entries.size
    }
    val handheldType = getHandheldType()

    Scaffold(
        contentWindowInsets = WindowInsets(16.dp, 16.dp, 16.dp, 16.dp),
        topBar = {
            Column {
                AppBar(device, goToSetup = goToSetup)
                Tabs(tabState, device)
            }
        },
        bottomBar = {
            if (!isTablet) {
                BottomNavBar(hasWatch, navController)
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton({
                Text("Infos hochladen")
            },
                { Icon(ImageVector.vectorResource(R.drawable.ic_upload), "Infos hochladen") },
                { UploadData(handheldType, device, context) })
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        HorizontalPager(state = tabState, modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (SelectedTab.entries[it]) {
                SelectedTab.Hardware -> Column(modifier = Modifier.fillMaxSize()) {
                    HardwareInformation(device)
                }

                SelectedTab.Storage -> Column(modifier = Modifier.fillMaxSize()) {
                    StorageInformation(device)
                }

                SelectedTab.Software -> Column(modifier = Modifier.fillMaxSize()) {
                    SoftwareInformation(device)
                }
            }
        }
    }
}
