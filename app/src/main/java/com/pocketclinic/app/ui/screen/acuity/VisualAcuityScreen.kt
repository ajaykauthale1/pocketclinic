package com.pocketclinic.app.ui.screen.acuity

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/** LogMAR → approximate Snellen denominator (rounded) */
private fun logmarToSnellen(logmar: Float): String {
    val denominator = 20 * Math.pow(10.0, logmar.toDouble()).toInt()
    return "20/$denominator"
}

@Composable
fun VisualAcuityScreen(
    modifier: Modifier = Modifier,
    viewModel: VisualAcuityViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("acuity_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (state.isComplete) {
            // Results view
            Text(
                text  = "TEST COMPLETE",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primaryContainer,
            )
            Spacer(Modifier.height(16.dp))
            state.finalLogmar?.let { logmar ->
                Text(
                    text  = logmarToSnellen(logmar),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.testTag("final_snellen"),
                )
                Text(
                    text  = "LogMAR %.2f".format(logmar),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Spacer(Modifier.height(32.dp))
            Button(onClick = viewModel::reset, modifier = Modifier.testTag("reset_button")) {
                Text("[ ↺ ] RETRY")
            }
        } else {
            // Active test view
            Text(
                text  = "Line: ${logmarToSnellen(state.currentLogmar)}",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.testTag("current_line"),
            )
            Text(
                text  = "Response ${state.responseCount + 1} / 10",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(32.dp))
            // Optotype placeholder — shows letters for user to read
            Text(
                text      = "[ E F P  Z L P D ]",
                style     = MaterialTheme.typography.bodyLarge,
                color     = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier  = Modifier
                    .fillMaxWidth()
                    .testTag("optotype"),
            )
            Spacer(Modifier.height(32.dp))
            Row(
                modifier            = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OutlinedButton(
                    onClick  = { viewModel.recordResponse(correct = false) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("wrong_button"),
                ) {
                    Text("[ ✗ ] WRONG")
                }
                Button(
                    onClick  = { viewModel.recordResponse(correct = true) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("correct_button"),
                ) {
                    Text("[ ✓ ] CORRECT")
                }
            }
        }
    }
}
