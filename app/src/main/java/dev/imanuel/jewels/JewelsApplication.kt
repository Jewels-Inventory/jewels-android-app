package dev.imanuel.jewels

import android.app.Application
import dev.imanuel.jewels.detection.detectionModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin

class JewelsApplication : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@JewelsApplication)
            workManagerFactory()
            modules(detectionModule)
        }
    }
}