package dev.imanuel.jewels.pages

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.widget.Toast
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dev.imanuel.jewels.api.cacheOneTimePasswords
import dev.imanuel.jewels.detection.SendDataWorker
import dev.imanuel.jewels.detection.information.Device
import dev.imanuel.jewels.detection.information.DeviceType
import io.ktor.client.HttpClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import org.koin.compose.koinInject

enum class HandheldType {
    Tablet,
    Smartphone;

    override fun toString(): String {
        return if (this == Tablet) {
            "Tablet"
        } else {
            "Smartphone"
        }
    }
}

@Composable
fun getHandheldType(): HandheldType {
    return if (currentWindowAdaptiveInfo().windowSizeClass.isWidthAtLeastBreakpoint(600)) {
        HandheldType.Tablet
    } else {
        HandheldType.Smartphone
    }
}

fun uploadData(handheldType: HandheldType, device: Device, context: Context) {
    val wearableRequest = OneTimeWorkRequestBuilder<SendDataWorker>().setInputData(
        Data.Builder().putString("data", Json.encodeToString(Device.serializer(), device))
            .build()
    ).build()
    WorkManager.getInstance(context).enqueue(wearableRequest)
    Toast.makeText(
        context, if (device.type == DeviceType.Handheld) {
            "Infos vom $handheldType ${device.model} werden hochgeladen"
        } else {
            "Infos von der Watch ${device.model} werden hochgeladen"
        }, Toast.LENGTH_LONG
    )
        .show()
}

fun Context.networkStatusFlow(): Flow<Boolean> {
    val cm = getSystemService(ConnectivityManager::class.java)

    fun isOnlineNow(): Boolean {
        val active = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(active) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    return callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(isOnlineNow())
            }

            override fun onLost(network: Network) {
                trySend(isOnlineNow())
            }

            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                trySend(isOnlineNow())
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        trySend(isOnlineNow())

        cm.registerNetworkCallback(request, callback)
        awaitClose { cm.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()
}

enum class Reachability {
    Offline,
    Checking,
    Reachable,
    Unreachable,
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun rememberReachability(
    httpClient: HttpClient = koinInject(),
    probeIntervalMs: Long = 15_000L,
): State<Reachability> {
    val context = LocalContext.current.applicationContext

    val reachabilityFlow = remember(httpClient, probeIntervalMs) {
        context
            .networkStatusFlow()
            .distinctUntilChanged()
            .flatMapLatest { online ->
                if (!online) {
                    flowOf(Reachability.Offline)
                } else {
                    flow {
                        emit(Reachability.Checking)
                        while (currentCoroutineContext().isActive) {
                            val (ok, _) = cacheOneTimePasswords(httpClient, context)
                            emit(if (ok) Reachability.Reachable else Reachability.Unreachable)
                            delay(probeIntervalMs)
                        }
                    }
                }
            }
            .distinctUntilChanged()
    }

    return reachabilityFlow.collectAsStateWithLifecycle(initialValue = Reachability.Checking)
}