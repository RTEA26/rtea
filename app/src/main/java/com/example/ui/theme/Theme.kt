package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val RteaLightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color(0xFFFFFEFC),
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = SecondaryTerracotta,
    onSecondary = Color(0xFFFFFEFC),
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryFixed,
    tertiary = TertiaryMint,
    onTertiary = Color(0xFFFFFEFC),
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryFixed,
    background = CanvasIvory,
    onBackground = TextPrimary,
    surface = CanvasIvory,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceHighest,
    onSurfaceVariant = TextMuted,
    surfaceContainerLowest = CardSurface,
    surfaceContainerLow = SurfaceLow,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceHigh,
    surfaceContainerHighest = SurfaceHighest,
    outline = OutlineLabel,
    outlineVariant = OutlineBorder,
    error = ErrorRed,
    onError = Color(0xFFFFFEFC),
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer
)

val RteaShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun RteaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RteaLightColorScheme,
        typography = RteaTypography,
        shapes = RteaShapes,
        content = content
    )
}
