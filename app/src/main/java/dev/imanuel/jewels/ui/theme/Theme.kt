package dev.imanuel.jewels.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dev.imanuel.jewels.shared.theme.backgroundDark
import dev.imanuel.jewels.shared.theme.backgroundLight
import dev.imanuel.jewels.shared.theme.errorContainerDark
import dev.imanuel.jewels.shared.theme.errorContainerLight
import dev.imanuel.jewels.shared.theme.errorDark
import dev.imanuel.jewels.shared.theme.errorLight
import dev.imanuel.jewels.shared.theme.inverseOnSurfaceDark
import dev.imanuel.jewels.shared.theme.inverseOnSurfaceLight
import dev.imanuel.jewels.shared.theme.inversePrimaryDark
import dev.imanuel.jewels.shared.theme.inversePrimaryLight
import dev.imanuel.jewels.shared.theme.inverseSurfaceDark
import dev.imanuel.jewels.shared.theme.inverseSurfaceLight
import dev.imanuel.jewels.shared.theme.onBackgroundDark
import dev.imanuel.jewels.shared.theme.onBackgroundLight
import dev.imanuel.jewels.shared.theme.onErrorContainerDark
import dev.imanuel.jewels.shared.theme.onErrorContainerLight
import dev.imanuel.jewels.shared.theme.onErrorDark
import dev.imanuel.jewels.shared.theme.onErrorLight
import dev.imanuel.jewels.shared.theme.onPrimaryContainerDark
import dev.imanuel.jewels.shared.theme.onPrimaryContainerLight
import dev.imanuel.jewels.shared.theme.onPrimaryDark
import dev.imanuel.jewels.shared.theme.onPrimaryLight
import dev.imanuel.jewels.shared.theme.onSecondaryContainerDark
import dev.imanuel.jewels.shared.theme.onSecondaryContainerLight
import dev.imanuel.jewels.shared.theme.onSecondaryDark
import dev.imanuel.jewels.shared.theme.onSecondaryLight
import dev.imanuel.jewels.shared.theme.onSurfaceDark
import dev.imanuel.jewels.shared.theme.onSurfaceLight
import dev.imanuel.jewels.shared.theme.onSurfaceVariantDark
import dev.imanuel.jewels.shared.theme.onSurfaceVariantLight
import dev.imanuel.jewels.shared.theme.onTertiaryContainerDark
import dev.imanuel.jewels.shared.theme.onTertiaryContainerLight
import dev.imanuel.jewels.shared.theme.onTertiaryDark
import dev.imanuel.jewels.shared.theme.onTertiaryLight
import dev.imanuel.jewels.shared.theme.outlineDark
import dev.imanuel.jewels.shared.theme.outlineLight
import dev.imanuel.jewels.shared.theme.outlineVariantDark
import dev.imanuel.jewels.shared.theme.outlineVariantLight
import dev.imanuel.jewels.shared.theme.primaryContainerDark
import dev.imanuel.jewels.shared.theme.primaryContainerLight
import dev.imanuel.jewels.shared.theme.primaryDark
import dev.imanuel.jewels.shared.theme.primaryLight
import dev.imanuel.jewels.shared.theme.scrimDark
import dev.imanuel.jewels.shared.theme.scrimLight
import dev.imanuel.jewels.shared.theme.secondaryContainerDark
import dev.imanuel.jewels.shared.theme.secondaryContainerLight
import dev.imanuel.jewels.shared.theme.secondaryDark
import dev.imanuel.jewels.shared.theme.secondaryLight
import dev.imanuel.jewels.shared.theme.surfaceBrightDark
import dev.imanuel.jewels.shared.theme.surfaceBrightLight
import dev.imanuel.jewels.shared.theme.surfaceContainerDark
import dev.imanuel.jewels.shared.theme.surfaceContainerHighDark
import dev.imanuel.jewels.shared.theme.surfaceContainerHighLight
import dev.imanuel.jewels.shared.theme.surfaceContainerHighestDark
import dev.imanuel.jewels.shared.theme.surfaceContainerHighestLight
import dev.imanuel.jewels.shared.theme.surfaceContainerLight
import dev.imanuel.jewels.shared.theme.surfaceContainerLowDark
import dev.imanuel.jewels.shared.theme.surfaceContainerLowLight
import dev.imanuel.jewels.shared.theme.surfaceContainerLowestDark
import dev.imanuel.jewels.shared.theme.surfaceContainerLowestLight
import dev.imanuel.jewels.shared.theme.surfaceDark
import dev.imanuel.jewels.shared.theme.surfaceDimDark
import dev.imanuel.jewels.shared.theme.surfaceDimLight
import dev.imanuel.jewels.shared.theme.surfaceLight
import dev.imanuel.jewels.shared.theme.surfaceVariantDark
import dev.imanuel.jewels.shared.theme.surfaceVariantLight
import dev.imanuel.jewels.shared.theme.tertiaryContainerDark
import dev.imanuel.jewels.shared.theme.tertiaryContainerLight
import dev.imanuel.jewels.shared.theme.tertiaryDark
import dev.imanuel.jewels.shared.theme.tertiaryLight

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
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
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

@Composable
fun JewelsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && dynamicColor) {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(
            context
        )
    } else when {
        darkTheme -> darkScheme
        else -> lightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

