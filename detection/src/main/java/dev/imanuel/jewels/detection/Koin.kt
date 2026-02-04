package dev.imanuel.jewels.detection

import dev.imanuel.jewels.detection.information.ApiClient
import dev.imanuel.jewels.detection.information.InformationCollector
import dev.imanuel.jewels.detection.information.InformationCollectorImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val detectionModule = module {
    single<InformationCollector> {
        InformationCollectorImpl(androidContext())
    }
    factory {
        val settings = loadSettings(androidContext());
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json()
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
                val server = settings?.host ?: "https://jewels.ulbricht.cloud"
                url("$server/api/")
            }
            engine {
                config {
                    followRedirects(true)
                }
            }
        }
    }
    single {
        ApiClient(androidContext(), get())
    }
    factory<ServerSettings?> {
        loadSettings(androidContext())
    }
    worker { SendDataWorker(get(), get(), get(), get()) }
}
