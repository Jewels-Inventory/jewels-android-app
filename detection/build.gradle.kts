plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.serialization)
}

android {
    namespace = "dev.imanuel.jewels.detection"
    compileSdk = 36

    defaultConfig {
        minSdk = 26

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            isJniDebuggable = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.android)

    implementation(platform(libs.io.insert.koin.koin.bom))
    implementation(libs.io.insert.koin.koin.core)
    implementation(libs.io.insert.koin.koin.androidx.workmanager)

    api(libs.retrofit)
    api(libs.okhttp)
    api(libs.androidx.work.runtime)
}
