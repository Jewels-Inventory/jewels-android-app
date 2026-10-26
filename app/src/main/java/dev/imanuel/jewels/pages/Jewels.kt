package dev.imanuel.jewels.pages

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.imanuel.jewels.R
import dev.imanuel.jewels.pages.components.LoadingFailed
import dev.imanuel.jewels.pages.components.TopBarActions
import dev.imanuel.jewels.shared.api.ApiClient
import dev.imanuel.jewels.shared.information.Device
import dev.imanuel.jewels.shared.information.DeviceType
import org.koin.compose.koinInject

@Composable
fun Jewels(
    handheld: Device?,
    watch: Device?,
    navController: NavController,
    apiClient: ApiClient = koinInject()
) {
    val handheldType = getHandheldType()
    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(true) }
    var loadingFailed by remember { mutableStateOf(false) }
    var myJewels by remember { mutableStateOf(emptyList<Device>()) }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            try {
                myJewels = apiClient.getMyJewels()
                loadingFailed = false
            } catch (e: Exception) {
                loadingFailed = true
            }
            isLoading = false
        }
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
                                context.uploadData(handheldType, watch)
                            }
                            if (handheld != null) {
                                context.uploadData(handheldType, handheld)
                            }
                        }) {
                        Icon(
                            ImageVector.vectorResource(R.drawable.ic_upload),
                            "Alle infos hochladen"
                        )
                    }
                    TopBarActions(navController = navController)
                })
        },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { isLoading = true },
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (loadingFailed) {
                LoadingFailed()
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
