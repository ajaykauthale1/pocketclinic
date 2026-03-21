package com.pocketclinic.app.ui.tests

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavController
import com.pocketclinic.app.ui.theme.Background
import com.pocketclinic.app.ui.theme.PrimaryAmber
import com.pocketclinic.app.ui.theme.SecondaryText
import com.pocketclinic.app.ui.theme.Surface
import com.pocketclinic.app.ui.theme.SurfaceContainerHigh

private data class TestCard(
    val title: String,
    val description: String,
    val sensorCheck: String,
    val instructions: String,
    val route: String,
    val tag: String,
)

private val TEST_CARDS = listOf(
    TestCard(
        title        = "VASCULAR PULSE",
        description  = "Heart rate + HRV via camera PPG",
        sensorCheck  = "Camera (rear)",
        instructions = "Place your fingertip over the rear camera lens.\nRemain still for 60 seconds.",
        route        = "vascular_pulse",
        tag          = "card_vascular",
    ),
    TestCard(
        title        = "NEURAL STEADY",
        description  = "Tremor frequency via gyroscope",
        sensorCheck  = "Gyroscope / IMU",
        instructions = "Hold the device with both hands, arms extended.\nRemain still for 30 seconds.",
        route        = "neural_steady",
        tag          = "card_neuro",
    ),
    TestCard(
        title        = "VISUAL ACUITY",
        description  = "ETDRS letter-chart staircase",
        sensorCheck  = "Front camera (face distance)",
        instructions = "Hold the device at arm's length.\nTap the quadrant where the opening of the E faces.",
        route        = "visual_acuity",
        tag          = "card_vision",
    ),
    TestCard(
        title        = "HEARING THRESHOLD",
        description  = "Pure-tone audiogram 500–8000 Hz",
        sensorCheck  = "Headphones required",
        instructions = "Put on headphones and sit in a quiet room.\nTap HEARD each time you detect a tone.",
        route        = "hearing",
        tag          = "card_hearing",
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestsScreen(navController: NavController) {
    var modalCard by remember { mutableStateOf<TestCard?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
            .testTag("tests_screen"),
    ) {
        Text(
            text  = "TESTS",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
        )

        Spacer(Modifier.height(16.dp))

        TEST_CARDS.forEach { card ->
            TestCardItem(
                card    = card,
                onClick = { modalCard = card },
            )
            Spacer(Modifier.height(12.dp))
        }
    }

    // Show TestInitModal when a card is tapped
    modalCard?.let { card ->
        TestInitModal(
            title        = card.title,
            instructions = card.instructions,
            sensorCheck  = card.sensorCheck,
            onStartTest  = {
                modalCard = null
                navController.navigate(card.route)
            },
            onDismiss    = { modalCard = null },
        )
    }
}

@Composable
private fun TestCardItem(
    card: TestCard,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface)
            .border(width = 1.dp, color = SurfaceContainerHigh)
            .clickable(onClick = onClick)
            .padding(16.dp)
            .testTag(card.tag),
    ) {
        Text(
            text  = card.title,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text  = card.description,
            style = MaterialTheme.typography.bodySmall,
            color = SecondaryText,
        )
    }
}
