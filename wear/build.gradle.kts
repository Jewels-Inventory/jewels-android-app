plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
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
    compileSdk = 35

    defaultConfig {
        applicationId = "dev.imanuel.jewels"
        minSdk = 26
        targetSdk = 35
        versionCode = computeVersionCode()
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile =
                file(System.getenv("ANDROID_KEY_STOREFILE") ?: "/opt/secure/signing-key-jewels.jks")
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

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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

    implementation(libs.play.services.wearable)
    implementation(libs.androidx.wear.compose.material)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.compose.ui.ui)
    implementation(libs.androidx.compose.ui.ui.graphics)
    implementation(libs.androidx.compose.material3.material3)

    implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.android)
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.core.splashscreen)

    implementation(platform(libs.io.insert.koin.koin.bom))
    implementation(libs.io.insert.koin.koin.core)
    implementation(libs.io.insert.koin.koin.compose)
    implementation(libs.io.insert.koin.koin.android)
    implementation(libs.io.insert.koin.koin.androidx.workmanager)
}
