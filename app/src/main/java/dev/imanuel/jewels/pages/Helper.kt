package dev.imanuel.jewels.pages

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowWidthSizeClass

@Composable
fun getDeviceType(): String {
    return if (currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT) {
        "Tablet"
    } else {
        "Smartphone"
    }
}