@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package dev.imanuel.jewels.pages

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.wearable.Wearable
import dev.imanuel.jewels.R
import dev.imanuel.jewels.detection.ServerSettings
import dev.imanuel.jewels.detection.information.Device
import dev.imanuel.jewels.pages.components.BottomNavBar
import dev.imanuel.jewels.pages.components.TopBarActions
import org.koin.compose.koinInject

@Composable
fun Jewels(
    serverSettings: ServerSettings = koinInject(),
    context: Context = koinInject(),
    handheld: Device?,
    watch: Device?,
    navController: NavController,
    goToSetup: () -> Unit
) {
    val handheldType = getHandheldType()

    Scaffold(
        contentWindowInsets = WindowInsets(16.dp, 16.dp, 16.dp, 16.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text("Jewels")
                },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    TopBarActions(goToSetup = goToSetup)
                })
        },
        bottomBar = {
            BottomNavBar(watch != null, navController)
        },
        floatingActionButton = {
            ExtendedFloatingActionButton({
                Text("Alle Infos hochladen")
            },
                { Icon(ImageVector.vectorResource(R.drawable.ic_upload), "Infos hochladen") },
                {
                    if (watch != null) {
                        UploadData(handheldType, watch, context)
                    }
                    if (handheld != null) {
                        UploadData(handheldType, handheld, context)
                    }
                })
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            Text("Verbunden", style = MaterialTheme.typography.displayMedium)
            Text(
                "Du bist mit ${serverSettings.host.replace("https://", "")} verbunden",
            )
        }
    }
}
