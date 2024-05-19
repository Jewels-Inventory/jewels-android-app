package dev.imanuel.jewels.detection

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import dev.imanuel.jewels.detection.information.ApiService
import dev.imanuel.jewels.detection.information.InformationCollector
import dev.imanuel.jewels.detection.information.InformationCollectorImpl
import dev.imanuel.jewels.detection.information.getRetrofit
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val detectionModule = module {
    single<InformationCollector> {
        InformationCollectorImpl(androidContext())
    }
    factory<ServerSettings?> {
        loadSettings(androidContext())
    }
    factory<ApiService?> {
        val settings = getOrNull<ServerSettings>()
        if (settings != null) {
            getRetrofit(settings)
                .create(ApiService::class.java)
        } else {
            null
        }
    }
    worker { SendDataWorker(getOrNull(), getOrNull(), get(), get(), get()) }
}
