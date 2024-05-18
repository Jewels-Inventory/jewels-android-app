package dev.imanuel.jewels

import android.app.Application
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import dev.imanuel.jewels.information.ApiService
import dev.imanuel.jewels.information.InformationCollector
import dev.imanuel.jewels.information.InformationCollectorImpl
import dev.imanuel.jewels.information.getRetrofit
import dev.imanuel.jewels.utils.ServerSettings
import dev.imanuel.jewels.utils.loadSettings
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.dsl.worker
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.dsl.module

val appModule = module {
    single<InformationCollector> { InformationCollectorImpl(androidContext()) }
    factory<ServerSettings?> {
        loadSettings(androidContext())
    }
    factory<ApiService?> {
        val settings = getOrNull<ServerSettings>()
        if (settings != null) {
            getRetrofit(settings).create(ApiService::class.java)
        } else {
            null
        }
    }
    factory<ImageAnalysis> { ImageAnalysis.Builder().build() }
    factory<ImageCapture> { ImageCapture.Builder().build() }
    worker { SendDataWorker(get(), get(), get(), get(), get()) }
}

class JewelsApplication : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@JewelsApplication)
            workManagerFactory()
            modules(appModule)
        }
    }
}