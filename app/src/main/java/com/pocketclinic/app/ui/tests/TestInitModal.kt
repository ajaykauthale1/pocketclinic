package com.pocketclinic.app.ui.tests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.pocketclinic.app.ui.theme.OnPrimary
import com.pocketclinic.app.ui.theme.PrimaryAmber
import com.pocketclinic.app.ui.theme.SecondaryText
import com.pocketclinic.app.ui.theme.Surface

/**
 * Shared bottom-sheet shown before starting any individual sensor test.
 *
 * @param title          Test name displayed as the sheet headline.
 * @param instructions   Multi-line instruction text shown to the patient.
 * @param sensorCheck    Short string describing the required hardware (e.g. "Camera", "Gyroscope").
 * @param onStartTest    Called when the patient taps START TEST.
 * @param onDismiss      Called when the sheet is dismissed without starting.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestInitModal(
    title: String,
    instructions: String,
    sensorCheck: String,
    onStartTest: () -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(),
) {
    ModalBottomSheet(
        onDismissRequest  = onDismiss,
        sheetState        = sheetState,
        containerColor    = Surface,
        modifier          = Modifier.testTag("test_init_modal"),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface)
                .padding(horizontal = 24.dp)
                .navigationBarsPadding(),
        ) {
            Text(
                text     = title,
                style    = MaterialTheme.typography.headlineLarge,
                color    = Color.White,
                modifier = Modifier.testTag("modal_title"),
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text  = instructions,
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryText,
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text  = "SENSOR  ›  $sensorCheck",
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText,
                modifier = Modifier.testTag("sensor_check_label"),
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick  = onStartTest,
                colors   = ButtonDefaults.buttonColors(
                    containerColor = PrimaryAmber,
                    contentColor   = OnPrimary,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_start_test"),
            ) {
                Text("[ ● ] START TEST", style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
