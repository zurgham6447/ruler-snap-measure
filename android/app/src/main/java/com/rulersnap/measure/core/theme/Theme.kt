package com.rulersnap.measure.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val RulerColorScheme = lightColorScheme(
    primary = PrimaryOlive,
    onPrimary = SurfaceCard,
    primaryContainer = SecondarySage,
    onPrimaryContainer = TextPrimary,
    secondary = AccentBlue,
    onSecondary = SurfaceCard,
    secondaryContainer = SurfaceSunken,
    onSecondaryContainer = TextPrimary,
    tertiary = MarkerRed,
    onTertiary = SurfaceCard,
    background = BackgroundPaper,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceSunken,
    onSurfaceVariant = TextSecondary,
    outline = OutlineSoft,
    outlineVariant = OutlineSoft,
    error = MarkerRed,
    onError = SurfaceCard
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RulerColorScheme,
        typography = AppTypography,
        content = content
    )
}
