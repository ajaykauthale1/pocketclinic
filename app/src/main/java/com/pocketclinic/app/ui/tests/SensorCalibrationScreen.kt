package com.pocketclinic.app.ui.tests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.pocketclinic.app.domain.usecase.CheckupProgress
import com.pocketclinic.app.domain.usecase.TestType
import com.pocketclinic.app.ui.theme.Background
import com.pocketclinic.app.ui.theme.PrimaryAmber
import com.pocketclinic.app.ui.theme.SecondaryText

data class SensorStatus(
    val label: String,
    val calibrated: Boolean,
)

/**
 * Displays PPG / Accelerometer / ARCore readiness with progress bars.
 *
 * Shown during [CheckupProgress.Calibrating] at the start of a full checkup.
 */
@Composable
fun SensorCalibrationScreen(
    sensors: List<SensorStatus> = DEFAULT_SENSORS,
    currentProgress: CheckupProgress = CheckupProgress.Calibrating,
    onCancel: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp)
            .testTag("calibration_screen"),
    ) {
        Text(
            text  = "SENSOR CALIBRATION",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text  = "Preparing sensors for full checkup…",
            style = MaterialTheme.typography.bodyMedium,
            color = SecondaryText,
        )

        Spacer(Modifier.height(32.dp))

        sensors.forEach { sensor ->
            SensorRow(sensor)
            Spacer(Modifier.height(20.dp))
        }

        Spacer(Modifier.weight(1f))

        // Show which test is currently running (if calibration is done)
        if (currentProgress is CheckupProgress.RunningTest) {
            Text(
                text     = "RUNNING  ›  ${currentProgress.type.name}",
                style    = MaterialTheme.typography.labelSmall,
                color    = PrimaryAmber,
                modifier = Modifier.testTag("running_label"),
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SensorRow(sensor: SensorStatus) {
    val statusText  = if (sensor.calibrated) "[ ● CALIBRATED ]" else "[ ▲ REQUIRES SYNC ]"
    val statusColor = if (sensor.calibrated) Color.White else PrimaryAmber
    val progress    = if (sensor.calibrated) 1f else 0.3f

    Column(modifier = Modifier.testTag("sensor_${sensor.label.lowercase()}")) {
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text     = sensor.label,
                style    = MaterialTheme.typography.bodyMedium,
                color    = Color.White,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text  = statusText,
                style = MaterialTheme.typography.labelSmall,
                color = statusColor,
            )
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress         = { progress },
            modifier         = Modifier.fillMaxWidth(),
            color            = statusColor,
            trackColor       = Color.White.copy(alpha = 0.1f),
        )
    }
}

val DEFAULT_SENSORS = listOf(
    SensorStatus("PPG / Camera",    calibrated = false),
    SensorStatus("Accelerometer",   calibrated = false),
    SensorStatus("ARCore",          calibrated = false),
)
