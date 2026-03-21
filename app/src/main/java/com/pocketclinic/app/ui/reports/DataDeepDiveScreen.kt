package com.pocketclinic.app.ui.reports

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.pocketclinic.app.ui.theme.Background
import com.pocketclinic.app.ui.theme.OutlineVariant
import com.pocketclinic.app.ui.theme.PrimaryAmber
import com.pocketclinic.app.ui.theme.SecondaryText

/** Time period filter options. */
enum class TimePeriod(val label: String) {
    H24("24H"), D7("7D"), D30("30D"), ALL("ALL")
}

/**
 * Data Deep-Dive screen: 30-day activity chart + anomaly event log.
 *
 * @param sessions  All session summaries available (caller passes the full list).
 * @param onBack    Back navigation callback.
 */
@Composable
fun DataDeepDiveScreen(
    sessions: List<SessionSummary> = emptyList(),
    onBack: () -> Unit = {},
) {
    var period by remember { mutableStateOf(TimePeriod.D30) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .testTag("deep_dive_screen"),
    ) {
        // Top bar
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text  = "DATA DEEP-DIVE",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
            )
        }

        // Period toggle
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            TimePeriod.entries.forEach { tp ->
                val selected = tp == period
                Text(
                    text     = tp.label,
                    style    = MaterialTheme.typography.labelSmall,
                    color    = if (selected) PrimaryAmber else SecondaryText,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .testTag("period_${tp.name}")
                        .let { mod ->
                            if (selected) mod else mod
                        },
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Activity chart
        ActivityChart(
            sessions = sessions,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(horizontal = 16.dp)
                .testTag("activity_chart"),
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text     = "ANOMALY LOG",
            style    = MaterialTheme.typography.labelSmall,
            color    = SecondaryText,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(Modifier.height(4.dp))

        val flagged = sessions.filter { it.flagged }
        if (flagged.isEmpty()) {
            Text(
                text     = "NO ANOMALIES DETECTED",
                style    = MaterialTheme.typography.bodySmall,
                color    = SecondaryText,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .testTag("no_anomalies"),
            )
        } else {
            LazyColumn {
                itemsIndexed(flagged) { index, session ->
                    Text(
                        text     = "[ ▲ ]  ${session.label}  ·  ${session.startedAt}",
                        style    = MaterialTheme.typography.bodySmall,
                        color    = PrimaryAmber,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .testTag("anomaly_row_$index"),
                    )
                    HorizontalDivider(color = OutlineVariant, thickness = 0.5.dp)
                }
            }
        }
    }
}

/**
 * Simple 30-day bar chart: one vertical bar per session, height proportional to duration.
 */
@Composable
private fun ActivityChart(
    sessions: List<SessionSummary>,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        if (sessions.isEmpty()) return@Canvas
        val maxDuration = sessions.maxOf { it.durationMs }.coerceAtLeast(1L)
        val barWidth = (size.width / sessions.size.coerceAtLeast(1)).coerceAtMost(12.dp.toPx())
        val spacing = (size.width - barWidth * sessions.size) / (sessions.size + 1)

        sessions.forEachIndexed { i, session ->
            val x = spacing + i * (barWidth + spacing) + barWidth / 2
            val heightFraction = session.durationMs.toFloat() / maxDuration
            val barHeight = size.height * heightFraction
            val color = if (session.flagged) PrimaryAmber else Color.White.copy(alpha = 0.6f)

            drawLine(
                color       = color,
                start       = Offset(x, size.height),
                end         = Offset(x, size.height - barHeight),
                strokeWidth = barWidth,
                cap         = StrokeCap.Square,
            )
        }
    }
}
