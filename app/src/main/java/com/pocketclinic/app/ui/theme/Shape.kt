package com.pocketclinic.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Terminal-Clinical design paradigm: zero border radius everywhere.
 * Sharp corners reinforce the engineered, precise aesthetic.
 */
val PocketClinicShapes = Shapes(
    extraSmall = RoundedCornerShape(0.dp),
    small      = RoundedCornerShape(0.dp),
    medium     = RoundedCornerShape(0.dp),
    large      = RoundedCornerShape(0.dp),
    extraLarge = RoundedCornerShape(0.dp),
)
