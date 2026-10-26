package dev.imanuel.jewels.shared

import dev.imanuel.jewels.shared.api.ApiClient
import dev.imanuel.jewels.shared.api.EncryptedJsonDiskCache
import dev.imanuel.jewels.shared.information.InformationCollector
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.bearerAuth
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val detectionModule = module {
    single {
        InformationCollector(androidContext())
    }
    single {
        Json {
            ignoreUnknownKeys = true
        }
    }
    single {
        EncryptedJsonDiskCache(androidContext())
    }
    factory {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(get())
            }
            defaultRequest {
                val settings = androidContext().loadSettings()
                contentType(ContentType.Application.Json)
                bearerAuth(settings?.token ?: "")
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
        ApiClient(androidContext(), get(), get(), get())
    }
    factory<ServerSettings?> {
        androidContext().loadSettings()
    }
    worker { SendDataWorker(get(), get(), get(), get()) }
}
