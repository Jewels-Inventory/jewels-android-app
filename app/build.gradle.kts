import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin.Companion.kotlinNpmResolutionManager

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.serialization)
}

fun computeVersionName(): String {
    return System.getenv("CI_COMMIT_TAG") ?: "0.0.0"
}

fun computeVersionCode(): Int {
    val versionSplit = (System.getenv("CI_COMMIT_TAG") ?: "0.0.0").split(".")
    if (versionSplit.size != 3) {
        throw IllegalArgumentException("The version tag needs to be in the format major.minor.patch")
    }

    val major = versionSplit[0]
    val minor = versionSplit[1]
    val patch = versionSplit[2]

    val versionCode =
        buildString {
            append("10")
            append(
                ((major.toInt() * 100000) + (minor.toInt() * 10000) + patch.toInt()).toString(
                    10
                )
            )
        }.toInt()

    print("Versionname is ${major}.${minor}.${patch}")
    print("Versioncode is $versionCode")

    return versionCode
}

android {
    namespace = "dev.imanuel.jewels"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.imanuel.jewels"
        minSdk = 31
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_23
        targetCompatibility = JavaVersion.VERSION_23
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "2.2.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":detection"))

    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.compose.ui.ui)
    implementation(libs.androidx.compose.ui.ui.graphics)
    implementation(libs.androidx.compose.material3.material3)
    implementation(libs.androidx.navigation.navigation.compose)

    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.accompanist.permissions)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
    implementation(libs.androidx.adaptive.android)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.androidx.work.runtime)

    implementation(platform(libs.io.insert.koin.koin.bom))
    implementation(libs.io.insert.koin.koin.core)
    implementation(libs.io.insert.koin.koin.compose)
    implementation(libs.io.insert.koin.koin.android)
    implementation(libs.io.insert.koin.koin.androidx.compose)
    implementation(libs.io.insert.koin.koin.androidx.workmanager)
    implementation(libs.play.services.wearable)
    implementation(libs.androidx.material3.window.size)

    implementation(libs.barcode.scanning)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.android)
}
