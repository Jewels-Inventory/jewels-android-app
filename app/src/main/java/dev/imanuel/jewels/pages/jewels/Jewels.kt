@file:OptIn(ExperimentalMaterial3Api::class)

package dev.imanuel.jewels.pages.jewels

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.common.util.DeviceProperties
import dev.imanuel.jewels.R
import dev.imanuel.jewels.api.getMyJewels
import dev.imanuel.jewels.detection.information.Device
import dev.imanuel.jewels.detection.information.DeviceType
import dev.imanuel.jewels.pages.components.BottomNavBar
import dev.imanuel.jewels.pages.components.TopBarActions
import dev.imanuel.jewels.pages.format
import dev.imanuel.jewels.pages.getHandheldType
import dev.imanuel.jewels.pages.uploadData
import io.ktor.client.HttpClient
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import dev.imanuel.jewels.detection.information.Device as DeviceInformation

@Composable
fun Jewels(
    context: Context = koinInject(),
    httpClient: HttpClient = koinInject(),
    handheld: Device?,
    watch: Device?,
    navController: NavController,
    goToSetup: () -> Unit
) {
    val handheldType = getHandheldType()
    val coroutineScope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }
    var loadingFailed by remember { mutableStateOf(false) }
    var myJewels by remember { mutableStateOf(emptyList<DeviceInformation>()) }

    val loadJewels = {
        coroutineScope.launch {
            isLoading = true
            myJewels = getMyJewels(httpClient, context)
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadJewels()
    }

    Scaffold(
        contentWindowInsets = WindowInsets(16.dp, 16.dp, 16.dp, 16.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text("Jewels")
                },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                actions = {
                    IconButton(
                        onClick = {
                            if (watch != null) {
                                uploadData(handheldType, watch, context)
                            }
                            if (handheld != null) {
                                uploadData(handheldType, handheld, context)
                            }
                        }) {
                        Icon(
                            ImageVector.vectorResource(R.drawable.ic_upload),
                            "Alle infos hochladen"
                        )
                    }
                    TopBarActions(goToSetup = goToSetup)
                })
        },
        bottomBar = {
            if (!DeviceProperties.isTablet(context)) {
                BottomNavBar(navController)
            }
        },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { loadJewels() },
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (loadingFailed) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        "Laden fehlgeschlagen",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Image(
                        ImageBitmap.imageResource(R.drawable.loading_error),
                        contentDescription = "Laden fehlgeschlagen",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(400.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(myJewels) { jewel ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                Text(
                                    "${jewel.manufacturer} ${jewel.model}",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                if (jewel.os != null) {
                                    Text(
                                        "Betriebssystem",
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                    val version =
                                        if (jewel.os?.version == "Unbekannt" || jewel.os?.version == null) {
                                            ""
                                        } else {
                                            jewel.os!!.version!!
                                        }
                                    Text(
                                        "${jewel.os?.name} $version",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                if (jewel.type == DeviceType.Computer) {
                                    if (jewel.cpu != null) {
                                        Text(
                                            "Prozessor",
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                        Text(
                                            jewel.cpu?.model!!,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                    if (jewel.ram != null) {
                                        Text(
                                            "Arbeitsspeicher",
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                        Text(
                                            "${jewel.ram!! format ".2f"} GB",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                                if (jewel.storage != null) {
                                    Text(
                                        "Speicherplatz",
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                    Text(
                                        "${jewel.storage!! format ".2f"} GB",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
