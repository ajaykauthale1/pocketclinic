package com.pocketclinic.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.pocketclinic.app.R

// ── Google Fonts Provider ──────────────────────────────────────────────────────
private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage   = "com.google.android.gms",
    certificates      = R.array.com_google_android_gms_fonts_certs,
)

// ── Font Families ─────────────────────────────────────────────────────────────
// JetBrains Mono  → live data, BPM values, timestamps, status codes, labels
// Space Grotesk   → screen headlines, large titles
// Inter           → body text, instructions, descriptions

private val jetBrainsMonoFont = GoogleFont("JetBrains Mono")
val JetBrainsMono = FontFamily(
    Font(googleFont = jetBrainsMonoFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = jetBrainsMonoFont, fontProvider = provider, weight = FontWeight.Bold),
)

private val spaceGroteskFont = GoogleFont("Space Grotesk")
val SpaceGrotesk = FontFamily(
    Font(googleFont = spaceGroteskFont, fontProvider = provider, weight = FontWeight.Light),
    Font(googleFont = spaceGroteskFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = spaceGroteskFont, fontProvider = provider, weight = FontWeight.Bold),
)

private val interFont = GoogleFont("Inter")
val InterFont = FontFamily(
    Font(googleFont = interFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = interFont, fontProvider = provider, weight = FontWeight.Bold),
)

// ── Typography Scale ──────────────────────────────────────────────────────────
val PocketClinicTypography = Typography(
    // Big data numbers: "72 BPM", "20/20"
    displayLarge = TextStyle(
        fontFamily   = JetBrainsMono,
        fontWeight   = FontWeight.Bold,
        fontSize     = 72.sp,
        lineHeight   = 72.sp,
        letterSpacing = (-2).sp,
    ),
    displayMedium = TextStyle(
        fontFamily   = JetBrainsMono,
        fontWeight   = FontWeight.Bold,
        fontSize     = 48.sp,
        lineHeight   = 52.sp,
        letterSpacing = (-1).sp,
    ),
    displaySmall = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Normal,
        fontSize   = 36.sp,
        lineHeight = 44.sp,
    ),
    // Screen headlines
    headlineLarge = TextStyle(
        fontFamily   = SpaceGrotesk,
        fontWeight   = FontWeight.Bold,
        fontSize     = 40.sp,
        lineHeight   = 44.sp,
        letterSpacing = (-1).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Normal,
        fontSize   = 28.sp,
        lineHeight = 36.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Normal,
        fontSize   = 24.sp,
        lineHeight = 32.sp,
    ),
    // Card and section titles
    titleLarge = TextStyle(
        fontFamily   = JetBrainsMono,
        fontWeight   = FontWeight.Bold,
        fontSize     = 18.sp,
        lineHeight   = 28.sp,
        letterSpacing = 2.sp,
    ),
    titleMedium = TextStyle(
        fontFamily   = JetBrainsMono,
        fontWeight   = FontWeight.Bold,
        fontSize     = 16.sp,
        lineHeight   = 24.sp,
        letterSpacing = 1.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Normal,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
    ),
    // Body / instructions (Inter)
    bodyLarge = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Normal,
        fontSize   = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Normal,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Normal,
        fontSize   = 12.sp,
        lineHeight = 16.sp,
    ),
    // Sensor data overlays — mono, tracking widest
    labelLarge = TextStyle(
        fontFamily   = JetBrainsMono,
        fontWeight   = FontWeight.Bold,
        fontSize     = 12.sp,
        lineHeight   = 16.sp,
        letterSpacing = 2.sp,
    ),
    labelMedium = TextStyle(
        fontFamily   = JetBrainsMono,
        fontWeight   = FontWeight.Normal,
        fontSize     = 11.sp,
        lineHeight   = 16.sp,
        letterSpacing = 1.sp,
    ),
    labelSmall = TextStyle(
        fontFamily   = JetBrainsMono,
        fontWeight   = FontWeight.Normal,
        fontSize     = 10.sp,
        lineHeight   = 14.sp,
        letterSpacing = 1.5.sp,
    ),
)
