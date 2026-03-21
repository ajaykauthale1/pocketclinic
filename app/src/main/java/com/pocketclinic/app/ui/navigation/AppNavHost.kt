package com.pocketclinic.app.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pocketclinic.app.ui.command.CommandScreen
import com.pocketclinic.app.ui.profile.ProfileScreen
import com.pocketclinic.app.ui.reports.DataDeepDiveScreen
import com.pocketclinic.app.ui.reports.ReportsEvent
import com.pocketclinic.app.ui.reports.ReportsScreen
import com.pocketclinic.app.ui.reports.ReportsViewModel
import com.pocketclinic.app.ui.screen.acuity.VisualAcuityScreen
import com.pocketclinic.app.ui.screen.hearing.HearingScreen
import com.pocketclinic.app.ui.screen.tremor.NeuralSteadyScreen
import com.pocketclinic.app.ui.screen.vascular.VascularPulseScreen
import com.pocketclinic.app.ui.tests.TestsScreen
import com.pocketclinic.app.ui.theme.Background
import androidx.hilt.navigation.compose.hiltViewModel

private object Routes {
    const val COMMAND         = "command"
    const val TESTS           = "tests"
    const val REPORTS         = "reports"
    const val PROFILE         = "profile"
    // Sensor screens (launched from TestsScreen via TestInitModal)
    const val VASCULAR_PULSE  = "vascular_pulse"
    const val NEURAL_STEADY   = "neural_steady"
    const val VISUAL_ACUITY   = "visual_acuity"
    const val HEARING         = "hearing"
    // Deep-dive (launched from ReportsScreen)
    const val DATA_DEEP_DIVE  = "data_deep_dive/{sessionId}"
    fun deepDive(id: Long) = "data_deep_dive/$id"
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    Scaffold(
        containerColor = Background,
        bottomBar      = { BottomNavBar(navController) },
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Routes.COMMAND,
            modifier         = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // ── Bottom nav destinations ──────────────────────────────────────
            composable(Routes.COMMAND) {
                CommandScreen()
            }

            composable(Routes.TESTS) {
                TestsScreen(navController = navController)
            }

            composable(Routes.REPORTS) {
                val vm: ReportsViewModel = hiltViewModel()
                LaunchedEffect(Unit) {
                    vm.events.collect { event ->
                        when (event) {
                            is ReportsEvent.NavigateToDeepDive ->
                                navController.navigate(Routes.deepDive(event.sessionId))
                            is ReportsEvent.ExportRequested    -> { /* TODO: PDF export */ }
                        }
                    }
                }
                ReportsScreen(viewModel = vm)
            }

            composable(Routes.PROFILE) {
                ProfileScreen()
            }

            // ── Sensor test screens ──────────────────────────────────────────
            composable(Routes.VASCULAR_PULSE) {
                VascularPulseScreen(
                    modifier = Modifier.fillMaxSize(),
                )
            }

            composable(Routes.NEURAL_STEADY) {
                NeuralSteadyScreen(
                    modifier = Modifier.fillMaxSize(),
                )
            }

            composable(Routes.VISUAL_ACUITY) {
                VisualAcuityScreen(
                    modifier = Modifier.fillMaxSize(),
                )
            }

            composable(Routes.HEARING) {
                HearingScreen(
                    modifier = Modifier.fillMaxSize(),
                )
            }

            // ── Data Deep-Dive ───────────────────────────────────────────────
            composable(
                route     = Routes.DATA_DEEP_DIVE,
                arguments = listOf(navArgument("sessionId") { type = NavType.LongType }),
            ) {
                DataDeepDiveScreen(
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
