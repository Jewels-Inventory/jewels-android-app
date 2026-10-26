package dev.imanuel.jewels.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.dynamicColorScheme
import dev.imanuel.jewels.shared.theme.backgroundDark
import dev.imanuel.jewels.shared.theme.errorContainerDark
import dev.imanuel.jewels.shared.theme.errorDark
import dev.imanuel.jewels.shared.theme.onBackgroundDark
import dev.imanuel.jewels.shared.theme.onErrorContainerDark
import dev.imanuel.jewels.shared.theme.onErrorDark
import dev.imanuel.jewels.shared.theme.onPrimaryContainerDark
import dev.imanuel.jewels.shared.theme.onPrimaryDark
import dev.imanuel.jewels.shared.theme.onSecondaryContainerDark
import dev.imanuel.jewels.shared.theme.onSecondaryDark
import dev.imanuel.jewels.shared.theme.onSurfaceDark
import dev.imanuel.jewels.shared.theme.onSurfaceVariantDark
import dev.imanuel.jewels.shared.theme.onTertiaryContainerDark
import dev.imanuel.jewels.shared.theme.onTertiaryDark
import dev.imanuel.jewels.shared.theme.outlineDark
import dev.imanuel.jewels.shared.theme.outlineVariantDark
import dev.imanuel.jewels.shared.theme.primaryContainerDark
import dev.imanuel.jewels.shared.theme.primaryDark
import dev.imanuel.jewels.shared.theme.secondaryContainerDark
import dev.imanuel.jewels.shared.theme.secondaryDark
import dev.imanuel.jewels.shared.theme.surfaceContainerDark
import dev.imanuel.jewels.shared.theme.surfaceContainerHighDark
import dev.imanuel.jewels.shared.theme.surfaceContainerLowDark
import dev.imanuel.jewels.shared.theme.tertiaryContainerDark
import dev.imanuel.jewels.shared.theme.tertiaryDark

private val darkScheme = ColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    onSurface = onSurfaceDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
)

@Composable
fun JewelsTheme(content: @Composable () -> Unit) {
    val dynamicColorScheme = dynamicColorScheme(LocalContext.current)
    MaterialTheme(colorScheme = dynamicColorScheme ?: darkScheme) {
        content()
    }
}

