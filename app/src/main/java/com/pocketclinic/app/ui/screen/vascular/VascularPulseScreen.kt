package com.pocketclinic.app.ui.screen.vascular

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun VascularPulseScreen(
    modifier: Modifier = Modifier,
    viewModel: VascularPulseViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("vascular_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // BPM display
        Text(
            text    = state.bpm?.let { "%.0f".format(it) } ?: "---",
            style   = MaterialTheme.typography.displayLarge,
            color   = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.testTag("bpm_display"),
        )
        Text(
            text  = "BPM",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(8.dp))

        // HRV RMSSD
        state.rmssd?.let { rmssd ->
            Text(
                text  = "HRV  %.1f ms".format(rmssd),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.testTag("rmssd_display"),
            )
        }

        Spacer(Modifier.height(16.dp))

        // Waveform canvas
        WaveformCanvas(
            waveform = state.waveform,
            color    = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .testTag("waveform_canvas"),
        )

        Spacer(Modifier.height(24.dp))

        // Start / Stop button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Button(
                onClick  = {
                    if (state.isRunning) viewModel.stopMeasurement()
                    else viewModel.startMeasurement()
                },
                modifier = Modifier.testTag("toggle_button"),
            ) {
                Text(if (state.isRunning) "[ ■ ] STOP" else "[ ● ] START")
            }
        }
    }
}

@Composable
private fun WaveformCanvas(
    waveform: List<Float>,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        if (waveform.size < 2) return@Canvas
        val w = size.width
        val h = size.height
        val step = w / (waveform.size - 1)
        for (i in 0 until waveform.size - 1) {
            drawLine(
                color       = color,
                start       = Offset(i * step, h * (1f - waveform[i])),
                end         = Offset((i + 1) * step, h * (1f - waveform[i + 1])),
                strokeWidth = 2f,
                cap         = StrokeCap.Round,
            )
        }
    }
}
