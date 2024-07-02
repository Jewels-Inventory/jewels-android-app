plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.compose")
}

val jewelsMajorVersion: String by project
val jewelsMinorVersion: String by project

fun computeVersionCode(): Int {
    val jewelsReleaseVersion = System.getenv("CI_PIPELINE_IID") ?: "5"
    val versionCode =
        (jewelsMajorVersion.toInt() * 100000) + (jewelsMinorVersion.toInt() * 10000) + jewelsReleaseVersion.toInt()

    print("Versionname is ${jewelsMajorVersion}.${jewelsMinorVersion}.${jewelsReleaseVersion}")
    print("Versioncode is $versionCode")

    return versionCode
}

android {
    namespace = "dev.imanuel.jewels"
    compileSdk = 34

    defaultConfig {
        applicationId = "dev.imanuel.jewels"
        minSdk = 26
        targetSdk = 34
        versionCode = computeVersionCode()
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("ANDROID_KEY_STOREFILE") ?: "/opt/secure/signing-key-jewels.jks")
            storePassword = System.getenv("ANDROID_KEYSTORE_PASSWORD")
            keyAlias = System.getenv("ANDROID_KEY_ALIAS") ?: "key0"
            keyPassword = System.getenv("ANDROID_KEYSTORE_PASSWORD")
        }
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
            isCrunchPngs = true
            isShrinkResources = true
            isProfileable = false
            isJniDebuggable = false
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":detection"))

    implementation("com.google.android.gms:play-services-wearable:18.2.0")
    implementation("androidx.wear.compose:compose-material:1.3.1")
    implementation("androidx.wear.compose:compose-foundation:1.3.1")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.camera:camera-camera2:1.3.4")
    implementation("androidx.camera:camera-lifecycle:1.3.4")
    implementation("androidx.camera:camera-view:1.3.4")
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    implementation("androidx.compose.material3.adaptive:adaptive-android:1.0.0-beta04")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("androidx.work:work-runtime:2.9.0")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.core:core-splashscreen:1.0.1")

    implementation(platform("io.insert-koin:koin-bom:3.5.6"))
    implementation("io.insert-koin:koin-core")
    implementation("io.insert-koin:koin-core-coroutines")
    implementation("io.insert-koin:koin-compose")
    implementation("io.insert-koin:koin-android")
    implementation("io.insert-koin:koin-androidx-compose")
    implementation("io.insert-koin:koin-androidx-compose-navigation")
    implementation("io.insert-koin:koin-androidx-navigation")
    implementation("io.insert-koin:koin-androidx-workmanager")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("androidx.wear:wear-tooling-preview:1.0.0")
}