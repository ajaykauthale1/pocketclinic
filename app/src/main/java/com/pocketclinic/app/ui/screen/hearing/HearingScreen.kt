package com.pocketclinic.app.ui.screen.hearing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HearingScreen(
    modifier: Modifier = Modifier,
    viewModel: HearingViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("hearing_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (state.isComplete) {
            Text(
                text  = "AUDIOGRAM COMPLETE",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primaryContainer,
            )
            Spacer(Modifier.height(16.dp))
            state.results.entries.sortedBy { it.key }.forEach { (freq, db) ->
                Text(
                    text  = "%d Hz → %.0f dBHL".format(freq, db),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        } else {
            Text(
                text  = "%d Hz".format(state.currentFreqHz),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.testTag("freq_display"),
            )
            Text(
                text  = "%.0f dBHL".format(state.currentDb),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.testTag("db_display"),
            )
            Text(
                text  = "Frequency ${state.freqIndex + 1} / ${HearingViewModel.FREQUENCIES.size}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick  = viewModel::startTone,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("play_button"),
            ) {
                Text("[ ♪ ] PLAY TONE")
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier            = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OutlinedButton(
                    onClick  = { viewModel.recordHeard(heard = false) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("not_heard_button"),
                ) {
                    Text("[ ✗ ] NOT HEARD")
                }
                Button(
                    onClick  = { viewModel.recordHeard(heard = true) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("heard_button"),
                ) {
                    Text("[ ✓ ] HEARD")
                }
            }

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick  = viewModel::nextFrequency,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("next_freq_button"),
            ) {
                Text("[ → ] NEXT FREQUENCY")
            }
        }
    }
}
