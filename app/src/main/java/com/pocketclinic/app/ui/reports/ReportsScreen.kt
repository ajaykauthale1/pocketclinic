package com.pocketclinic.app.ui.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pocketclinic.app.ui.components.StatusShape
import com.pocketclinic.app.ui.theme.Background
import com.pocketclinic.app.ui.theme.OnPrimary
import com.pocketclinic.app.ui.theme.OutlineVariant
import com.pocketclinic.app.ui.theme.PrimaryAmber
import com.pocketclinic.app.ui.theme.SecondaryText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

private val DATE_FMT = SimpleDateFormat("yyyy-MM-dd  HH:mm", Locale.getDefault())

@Composable
fun ReportsScreen(
    navController: NavController? = null,
    viewModel: ReportsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .testTag("reports_screen"),
    ) {
        // Header
        Text(
            text     = "REPORTS",
            style    = MaterialTheme.typography.headlineLarge,
            color    = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        )

        if (state.isLoading) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("LOADING...", style = MaterialTheme.typography.bodyMedium, color = SecondaryText)
            }
        } else if (state.sessions.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    "NO SESSIONS IN LAST 30 DAYS",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText,
                    modifier = Modifier.testTag("empty_label"),
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
            ) {
                items(state.sessions, key = { it.id }) { session ->
                    SessionRow(
                        summary  = session,
                        onClick  = { viewModel.onSessionClicked(session.id) },
                    )
                    HorizontalDivider(color = OutlineVariant, thickness = 0.5.dp)
                }
            }
        }

        // Export button
        Button(
            onClick  = viewModel::onExportClicked,
            colors   = ButtonDefaults.buttonColors(
                containerColor = PrimaryAmber,
                contentColor   = OnPrimary,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("btn_export"),
        ) {
            Text("[ ■ ] EXPORT PDF", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun SessionRow(
    summary: SessionSummary,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("session_row_${summary.id}"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text  = DATE_FMT.format(Date(summary.startedAt)),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text  = "%s  ·  %s".format(summary.label, formatDuration(summary.durationMs)),
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryText,
            )
        }
        StatusShape(flagged = summary.flagged)
    }
}

private fun formatDuration(ms: Long): String {
    val mins = TimeUnit.MILLISECONDS.toMinutes(ms)
    val secs = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
    return "%02d:%02d".format(mins, secs)
}
