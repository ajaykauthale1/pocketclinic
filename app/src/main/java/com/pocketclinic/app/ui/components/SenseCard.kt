package com.pocketclinic.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.pocketclinic.app.ui.theme.PrimaryAmber
import com.pocketclinic.app.ui.theme.SecondaryText
import com.pocketclinic.app.ui.theme.Surface
import com.pocketclinic.app.ui.theme.SurfaceContainerHigh

/**
 * Reusable bento card for the Command Center 2×2 grid.
 *
 * @param title      Card label (e.g. "PULSE", "NEURO", "VISION", "AUDIO")
 * @param value      Primary display value (e.g. "72 BPM", "98.2%")
 * @param subtitle   Secondary line (e.g. "HRV 35 ms")
 * @param isLive     True when sensor is actively streaming
 * @param isAmber    True when value is flagged (amber highlight)
 * @param tag        Compose testTag for UI tests
 */
@Composable
fun SenseCard(
    title: String,
    value: String,
    subtitle: String = "",
    isLive: Boolean = false,
    isAmber: Boolean = false,
    tag: String = "",
    modifier: Modifier = Modifier,
) {
    val borderColor = when {
        isAmber -> PrimaryAmber
        isLive  -> Color.White.copy(alpha = 0.4f)
        else    -> SurfaceContainerHigh
    }

    val valueColor = if (isAmber) PrimaryAmber else Color.White

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Surface)
            .border(width = 1.dp, color = borderColor)
            .padding(12.dp)
            .then(if (tag.isNotEmpty()) Modifier.testTag(tag) else Modifier),
    ) {
        Text(
            text  = title,
            style = MaterialTheme.typography.labelSmall,
            color = SecondaryText,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text  = value,
            style = MaterialTheme.typography.headlineMedium,
            color = valueColor,
        )
        if (subtitle.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text  = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryText,
            )
        }
    }
}
