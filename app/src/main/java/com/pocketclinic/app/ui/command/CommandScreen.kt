package com.pocketclinic.app.ui.command

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pocketclinic.app.ui.components.SenseCard
import com.pocketclinic.app.ui.theme.Background
import com.pocketclinic.app.ui.theme.OnPrimary
import com.pocketclinic.app.ui.theme.PrimaryAmber

@Composable
fun CommandScreen(
    viewModel: CommandViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Background,
        contentWindowInsets = WindowInsets(0),
        floatingActionButton = {
            FloatingActionButton(
                onClick           = viewModel::startFullCheckup,
                containerColor    = PrimaryAmber,
                contentColor      = OnPrimary,
                modifier          = Modifier.testTag("fab_checkup"),
            ) {
                Icon(
                    imageVector  = Icons.Default.PlayArrow,
                    contentDescription = "Start Full Checkup",
                )
            }
        },
        modifier = Modifier.testTag("command_screen"),
    ) { innerPadding ->
        LazyVerticalGrid(
            columns         = GridCells.Fixed(2),
            contentPadding  = PaddingValues(12.dp),
            verticalArrangement   = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier        = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(innerPadding),
        ) {
            // PULSE — full-width span
            item(span = { GridItemSpan(2) }) {
                val pulse = state.pulseCard
                SenseCard(
                    title    = "PULSE",
                    value    = pulse.bpm?.let { "%.0f BPM".format(it) } ?: "--- BPM",
                    subtitle = pulse.rmssd?.let { "HRV  %.1f ms".format(it) } ?: "",
                    isLive   = pulse.isLive,
                    isAmber  = pulse.rmssd?.let { it < 20f } ?: false,
                    tag      = "card_pulse",
                    modifier = Modifier.aspectRatio(4f),
                )
            }

            // NEURO
            item {
                val neuro = state.neuroCard
                SenseCard(
                    title    = "NEURO",
                    value    = neuro.stabilityPercent?.let { "%.1f%%".format(it) } ?: "---%",
                    subtitle = neuro.dominantFreqHz?.let { "%.1f Hz".format(it) } ?: "",
                    isLive   = neuro.isLive,
                    tag      = "card_neuro",
                    modifier = Modifier.aspectRatio(1f),
                )
            }

            // VISION
            item {
                val vision = state.visionCard
                SenseCard(
                    title    = "VISION",
                    value    = vision.logmar?.let { "%.2f".format(it) } ?: "---",
                    subtitle = if (vision.isLive) "LogMAR" else "NO DATA",
                    isLive   = vision.isLive,
                    tag      = "card_vision",
                    modifier = Modifier.aspectRatio(1f),
                )
            }

            // AUDIO
            item(span = { GridItemSpan(2) }) {
                val audio = state.audioCard
                SenseCard(
                    title    = "AUDIO",
                    value    = audio.thresholdDbHl?.let { "%.0f dBHL".format(it) } ?: "--- dBHL",
                    subtitle = if (audio.isAmber) "[ ▲ ] ABOVE THRESHOLD" else "",
                    isLive   = audio.isLive,
                    isAmber  = audio.isAmber,
                    tag      = "card_audio",
                    modifier = Modifier.aspectRatio(4f),
                )
            }
        }
    }
}
