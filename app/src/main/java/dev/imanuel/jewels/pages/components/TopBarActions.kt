package dev.imanuel.jewels.pages.components

import android.util.Log
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.Wearable
import dev.imanuel.jewels.NavigationPage
import dev.imanuel.jewels.R
import dev.imanuel.jewels.shared.api.ApiClient
import dev.imanuel.jewels.shared.deleteSettings
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.compose.koinInject

@Composable
fun TopBarActions(
    navController: NavController,
    apiClient: ApiClient = koinInject()
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    IconButton(
        onClick = {
            apiClient.clearCache()
            context.deleteSettings()
            navController.navigate(NavigationPage.ServerSetup.name)

            coroutineScope.launch {
                try {
                    val dataClient by lazy { Wearable.getDataClient(context) }
                    dataClient.dataItems.await().filter { it.uri.path == "/settings" }.forEach {
                        dataClient.deleteDataItems(it.uri)
                    }
                } catch (e: ApiException) {
                    if (e.statusCode == 17) {
                        Log.i("TopBarActions", "TopBarActions: No wearable api available")
                    }
                }
            }
        },
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(ImageVector.vectorResource(R.drawable.ic_logout), "Abmelden")
    }
}