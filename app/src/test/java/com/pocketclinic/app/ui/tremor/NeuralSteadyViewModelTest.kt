package com.pocketclinic.app.ui.tremor

import com.google.common.truth.Truth.assertThat
import com.pocketclinic.app.sensor.tremor.TremorProcessor
import com.pocketclinic.app.sensor.tremor.TremorSample
import com.pocketclinic.app.ui.screen.tremor.NeuralSteadyViewModel
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
class NeuralSteadyViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun processor(vararg samples: TremorSample): TremorProcessor =
        mockk(relaxed = true) {
            every { sampleFlow } returns flowOf(*samples)
        }

    @Test
    fun `initial state has null frequency and zero amplitude`() = runTest {
        val vm = NeuralSteadyViewModel(processor())
        val state = vm.uiState.value
        assertThat(state.dominantFreqHz).isNull()
        assertThat(state.amplitude).isEqualTo(0f)
        assertThat(state.isRunning).isFalse()
    }

    @Test
    fun `dominantFreqHz is updated from tremor sample`() = runTest {
        val sample = TremorSample(dominantFreqHz = 5.2f, amplitude = 0.3f, fftBins = floatArrayOf())
        val vm = NeuralSteadyViewModel(processor(sample))
        vm.startMeasurement()
        advanceUntilIdle()
        assertThat(vm.uiState.value.dominantFreqHz).isWithin(0.01f).of(5.2f)
    }

    @Test
    fun `amplitude is updated from tremor sample`() = runTest {
        val sample = TremorSample(dominantFreqHz = 4.0f, amplitude = 0.65f, fftBins = floatArrayOf())
        val vm = NeuralSteadyViewModel(processor(sample))
        vm.startMeasurement()
        advanceUntilIdle()
        assertThat(vm.uiState.value.amplitude).isWithin(0.01f).of(0.65f)
    }

    @Test
    fun `fftBins are passed through to ui state`() = runTest {
        val bins = floatArrayOf(0.1f, 0.5f, 0.9f, 0.3f)
        val sample = TremorSample(dominantFreqHz = 3.0f, amplitude = 0.2f, fftBins = bins)
        val vm = NeuralSteadyViewModel(processor(sample))
        vm.startMeasurement()
        advanceUntilIdle()
        assertThat(vm.uiState.value.fftBins).hasLength(4)
    }

    @Test
    fun `isRunning toggles correctly`() = runTest {
        val vm = NeuralSteadyViewModel(processor())
        assertThat(vm.uiState.value.isRunning).isFalse()
        vm.startMeasurement()
        advanceUntilIdle()
        assertThat(vm.uiState.value.isRunning).isTrue()
        vm.stopMeasurement()
        advanceUntilIdle()
        assertThat(vm.uiState.value.isRunning).isFalse()
    }
}
