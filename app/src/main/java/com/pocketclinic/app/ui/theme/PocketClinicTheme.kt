package com.pocketclinic.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * PocketClinic always uses dark mode — forced regardless of system setting.
 * Reasons:
 *   1. Prevents pupil constriction during visual acuity tests.
 *   2. Reduces OLED battery drain during sensor measurement.
 *   3. WCAG AAA compliance for visually impaired users.
 *   4. Terminal-clinical aesthetic consistency.
 */
private val PocketClinicColorScheme = darkColorScheme(
    // Backgrounds
    background              = Background,
    surface                 = Surface,
    surfaceVariant          = SurfaceContainerHighest,
    surfaceContainer        = SurfaceContainer,
    surfaceContainerLow     = SurfaceContainerLow,
    surfaceContainerHigh    = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest,
    surfaceContainerLowest  = SurfaceContainerLowest,

    // Primary — Amber accent
    primary            = PrimaryAmberLight,
    onPrimary          = OnPrimary,
    primaryContainer   = PrimaryAmber,
    onPrimaryContainer = OnPrimary,

    // Secondary
    secondary            = Secondary,
    onSecondary          = Color(0xFF2F3131),
    secondaryContainer   = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,

    // Tertiary
    tertiary            = Tertiary,
    onTertiary          = OnTertiary,
    tertiaryContainer   = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,

    // Error
    error            = Error,
    onError          = OnError,
    errorContainer   = ErrorContainer,
    onErrorContainer = OnErrorContainer,

    // Text / icons on surfaces
    onBackground    = OnSurface,
    onSurface       = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,

    // Outlines
    outline        = Outline,
    outlineVariant = OutlineVariant,
)

@Composable
fun PocketClinicTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = PocketClinicColorScheme,
        typography  = PocketClinicTypography,
        shapes      = PocketClinicShapes,
        content     = content,
    )
}
