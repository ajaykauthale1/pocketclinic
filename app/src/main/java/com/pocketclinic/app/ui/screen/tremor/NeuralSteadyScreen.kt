package com.pocketclinic.app.ui.screen.tremor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun NeuralSteadyScreen(
    modifier: Modifier = Modifier,
    viewModel: NeuralSteadyViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("tremor_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Dominant frequency
        Text(
            text  = state.dominantFreqHz?.let { "%.1f Hz".format(it) } ?: "---",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.testTag("freq_display"),
        )
        Text(
            text  = "TREMOR FREQUENCY",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(24.dp))

        // Tremor circle indicator
        TremorCircle(
            amplitude = state.amplitude,
            modifier  = Modifier
                .size(160.dp)
                .testTag("tremor_circle"),
        )

        Spacer(Modifier.height(24.dp))

        // FFT bar graph
        FftBarGraph(
            bins     = state.fftBins,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .testTag("fft_graph"),
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick  = {
                if (state.isRunning) viewModel.stopMeasurement()
                else viewModel.startMeasurement()
            },
            modifier = Modifier.testTag("toggle_button"),
        ) {
            Text(if (state.isRunning) "[ ■ ] STOP" else "[ ▲ ] START")
        }
    }
}

@Composable
private fun TremorCircle(amplitude: Float, modifier: Modifier = Modifier) {
    val color = MaterialTheme.colorScheme.primaryContainer
    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2f * (0.3f + amplitude * 0.7f).coerceIn(0f, 1f)
        drawCircle(
            color  = color,
            radius = radius,
            center = Offset(size.width / 2, size.height / 2),
            style  = Stroke(width = 3f),
        )
    }
}

@Composable
private fun FftBarGraph(bins: FloatArray, modifier: Modifier = Modifier) {
    val color = MaterialTheme.colorScheme.primaryContainer
    Canvas(modifier = modifier) {
        if (bins.isEmpty()) return@Canvas
        val barWidth = size.width / bins.size
        bins.forEachIndexed { i, v ->
            val barH = size.height * v.coerceIn(0f, 1f)
            drawRect(
                color    = color,
                topLeft  = Offset(i * barWidth, size.height - barH),
                size     = Size(barWidth - 2f, barH),
            )
        }
    }
}
