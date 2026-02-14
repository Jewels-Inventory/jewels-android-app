@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package dev.imanuel.jewels

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import dev.imanuel.jewels.detection.ServerSettings
import dev.imanuel.jewels.detection.information.Device
import dev.imanuel.jewels.detection.information.DeviceType
import dev.imanuel.jewels.detection.information.InformationCollector
import dev.imanuel.jewels.detection.loadSettings
import dev.imanuel.jewels.pages.Login
import dev.imanuel.jewels.pages.OneTimePasswords
import dev.imanuel.jewels.pages.ServerSetup
import dev.imanuel.jewels.pages.components.NavRail
import dev.imanuel.jewels.pages.jewels.Jewels
import dev.imanuel.jewels.ui.theme.JewelsTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import org.koin.compose.koinInject

enum class NavigationPage {
    ServerSetup,
    Login,
    Jewels,
    OneTimePasswords
}

class MainActivity : ComponentActivity() {
    private val dataClient by lazy { Wearable.getDataClient(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val settings = loadSettings(this)
        if (settings != null) {
            val request = PutDataRequest.create("/settings").setUrgent().apply {
                data = Json.encodeToString(ServerSettings.serializer(), settings).toByteArray()
            }
            dataClient.putDataItem(request)
        }

        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val isTablet = windowSizeClass.widthSizeClass > WindowWidthSizeClass.Compact
            MainComposable(isTablet = isTablet)
        }
    }
}


@Composable
fun MainComposable(
    context: Context = koinInject(),
    collector: InformationCollector = koinInject(),
    isTablet: Boolean,
) {
    val navController = rememberNavController()
    val settings = loadSettings(context)
    val coroutineScope = rememberCoroutineScope()

    var watch by remember { mutableStateOf<Device?>(null) }
    var handheld by remember { mutableStateOf<Device?>(null) }

    LaunchedEffect(handheld) {
        coroutineScope.launch {
            handheld = collector.collect(DeviceType.Handheld)
        }
    }
    LaunchedEffect(watch) {
        coroutineScope.launch {
            try {
                val dataClient by lazy { Wearable.getDataClient(context) }
                val nodeClient by lazy { Wearable.getNodeClient(context) }

                if (nodeClient.connectedNodes.await().isNotEmpty()) {
                    dataClient
                        .dataItems
                        .await()
                        .firstOrNull { it.uri.path == "/wear-device" }
                        .let { item ->
                            if (item != null) {
                                val data = item.data
                                if (data != null) {
                                    watch = Json.decodeFromString(
                                        Device.serializer(),
                                        data.decodeToString()
                                    )
                                }
                            }

                            dataClient.addListener { events ->
                                events
                                    .filter { it.dataItem.uri.path == "/wear-device" }
                                    .forEach { event ->
                                        if (event.type == DataEvent.TYPE_CHANGED) {
                                            val data = event.dataItem.data
                                            watch = if (data != null) {
                                                Json.decodeFromString(
                                                    Device.serializer(),
                                                    data.decodeToString()
                                                )
                                            } else {
                                                null
                                            }
                                        } else if (event.type == DataEvent.TYPE_DELETED) {
                                            watch = null
                                        }
                                    }
                            }
                        }
                }
            } catch (e: ApiException) {
                if (e.statusCode == 17) {
                    Log.i("MainComposable", "MainComposable: No wearable api available")
                }
            }
        }
    }

    JewelsTheme {
        NavHost(
            navController = navController,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            startDestination = if (settings != null) {
                NavigationPage.Jewels.name
            } else {
                NavigationPage.ServerSetup.name
            },
        ) {
            composable(NavigationPage.Jewels.name) {
                NavRail(
                    navController = navController,
                ) {
                    Jewels(
                        watch = watch,
                        handheld = handheld,
                        navController = navController,
                        goToSetup = { navController.navigate(NavigationPage.ServerSetup.name) })
                }
            }
            composable(NavigationPage.OneTimePasswords.name) {
                NavRail(
                    navController = navController,
                ) {
                    OneTimePasswords(
                        navController = navController,
                        goToSetup = { navController.navigate(NavigationPage.ServerSetup.name) })
                }
            }
            composable(NavigationPage.ServerSetup.name) {
                ServerSetup(goToLogin = {
                    navController.navigate(
                        NavigationPage.Login.name
                    )
                })
            }
            composable(NavigationPage.Login.name) {
                Login(
                    goToJewels = { navController.navigate(NavigationPage.Jewels.name) },
                    goToSetup = { navController.navigate(NavigationPage.ServerSetup.name) })
            }
        }
    }
}
