package com.pocketclinic.app.ui.vascular

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.pocketclinic.app.sensor.ppg.PpgProcessor
import com.pocketclinic.app.sensor.ppg.PpgSample
import com.pocketclinic.app.ui.screen.vascular.VascularPulseViewModel
import com.pocketclinic.app.util.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VascularPulseViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun processor(vararg samples: PpgSample): PpgProcessor =
        mockk(relaxed = true) {
            every { sampleFlow } returns flowOf(*samples)
        }

    @Test
    fun `initial state is idle with null bpm`() = runTest {
        val vm = VascularPulseViewModel(processor())
        vm.uiState.test {
            val state = awaitItem()
            assertThat(state.bpm).isNull()
            assertThat(state.isRunning).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `bpm is updated when ppg sample has bpm value`() = runTest {
        val vm = VascularPulseViewModel(
            processor(PpgSample(bpm = 72f, rmssd = 35f, waveform = floatArrayOf()))
        )
        vm.startMeasurement()
        advanceUntilIdle()
        assertThat(vm.uiState.value.bpm).isEqualTo(72f)
    }

    @Test
    fun `rmssd is updated when ppg sample contains it`() = runTest {
        val vm = VascularPulseViewModel(
            processor(PpgSample(bpm = 60f, rmssd = 50f, waveform = floatArrayOf()))
        )
        vm.startMeasurement()
        advanceUntilIdle()
        assertThat(vm.uiState.value.rmssd).isEqualTo(50f)
    }

    @Test
    fun `isRunning is true after startMeasurement`() = runTest {
        val vm = VascularPulseViewModel(processor())
        vm.startMeasurement()
        advanceUntilIdle()
        assertThat(vm.uiState.value.isRunning).isTrue()
    }

    @Test
    fun `isRunning is false after stopMeasurement`() = runTest {
        val vm = VascularPulseViewModel(processor())
        vm.startMeasurement()
        advanceUntilIdle()
        vm.stopMeasurement()
        advanceUntilIdle()
        assertThat(vm.uiState.value.isRunning).isFalse()
    }

    @Test
    fun `waveform buffer is populated from ppg samples`() = runTest {
        val vm = VascularPulseViewModel(
            processor(PpgSample(bpm = 70f, rmssd = null, waveform = floatArrayOf(0.1f, 0.5f, 0.9f)))
        )
        vm.startMeasurement()
        advanceUntilIdle()
        assertThat(vm.uiState.value.waveform).isNotEmpty()
    }
}
