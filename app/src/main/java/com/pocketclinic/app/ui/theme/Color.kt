package com.pocketclinic.app.ui.theme

import androidx.compose.ui.graphics.Color

// ── Backgrounds & Surfaces ────────────────────────────────────────────────────
val Background              = Color(0xFF131313)  // True Black
val Surface                 = Color(0xFF1E1E1E)  // Dark Gray (elevated)
val SurfaceContainer        = Color(0xFF201F1F)
val SurfaceContainerLow     = Color(0xFF1C1B1B)
val SurfaceContainerHigh    = Color(0xFF2A2A2A)
val SurfaceContainerHighest = Color(0xFF353534)
val SurfaceContainerLowest  = Color(0xFF0E0E0E)
val SurfaceDim              = Color(0xFF131313)

// ── Primary Accent — Vivid Amber ─────────────────────────────────────────────
// Strictly reserved for: active states, START buttons, abnormal alerts
val PrimaryAmber      = Color(0xFFFFB300)  // #FFB300 — primary-container
val PrimaryAmberLight = Color(0xFFFFD79B)  // #FFD79B — primary
val PrimaryAmberFixed = Color(0xFFFFBA38)  // #FFBA38 — primary-fixed-dim
val OnPrimary         = Color(0xFF432C00)  // #432C00 — on-primary (on amber buttons)
val OnPrimaryFixed    = Color(0xFF281900)

// ── Text ──────────────────────────────────────────────────────────────────────
val PrimaryText   = Color(0xFFFFFFFF)  // Pure White — primary metrics / normal status
val SecondaryText = Color(0xFFAAAAAA)  // Light Gray — sensor data overlays / timestamps
// Alias to M3 token names:
val OnSurface        = Color(0xFFE5E2E1)
val OnSurfaceVariant = Color(0xFFD6C4AC)
val OnSecondaryContainer = Color(0xFFB8B8B8)

// ── Outline ───────────────────────────────────────────────────────────────────
val Outline        = Color(0xFF9E8E78)
val OutlineVariant = Color(0xFF514532)

// ── Secondary ─────────────────────────────────────────────────────────────────
val Secondary          = Color(0xFFC7C6C6)
val SecondaryContainer = Color(0xFF484949)

// ── Error ─────────────────────────────────────────────────────────────────────
val Error          = Color(0xFFFFB4AB)
val ErrorContainer = Color(0xFF93000A)
val OnError        = Color(0xFF690005)
val OnErrorContainer = Color(0xFFFFDAD6)

// ── Tertiary (accent complement) ──────────────────────────────────────────────
val Tertiary          = Color(0xFFA4E7FF)
val TertiaryContainer = Color(0xFF00D2FE)
val OnTertiary        = Color(0xFF003543)
val OnTertiaryContainer = Color(0xFF00566A)
