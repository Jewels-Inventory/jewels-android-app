package dev.imanuel.jewels

import android.app.Application
import dev.imanuel.jewels.detection.detectionModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::SettingsViewModel)
}

class JewelsApplication : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@JewelsApplication)
            workManagerFactory()
            modules(detectionModule, appModule)
        }
    }
}