package com.pocketclinic.app.ui.command

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.pocketclinic.app.sensor.ppg.PpgProcessor
import com.pocketclinic.app.sensor.ppg.PpgSample
import com.pocketclinic.app.sensor.tremor.TremorProcessor
import com.pocketclinic.app.sensor.tremor.TremorSample
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
class CommandViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun ppg(vararg samples: PpgSample): PpgProcessor =
        mockk(relaxed = true) { every { sampleFlow } returns flowOf(*samples) }

    private fun tremor(vararg samples: TremorSample): TremorProcessor =
        mockk(relaxed = true) { every { sampleFlow } returns flowOf(*samples) }

    private fun vm(
        ppgProcessor: PpgProcessor = ppg(),
        tremorProcessor: TremorProcessor = tremor(),
    ) = CommandViewModel(ppgProcessor, tremorProcessor)

    @Test
    fun `initial state has all cards idle`() = runTest {
        val viewModel = vm()
        val state = viewModel.uiState.value
        assertThat(state.pulseCard.isLive).isFalse()
        assertThat(state.neuroCard.isLive).isFalse()
        assertThat(state.visionCard.isLive).isFalse()
        assertThat(state.audioCard.isLive).isFalse()
    }

    @Test
    fun `pulse card shows bpm when ppg sample arrives`() = runTest {
        val viewModel = vm(ppg(PpgSample(bpm = 72f, rmssd = 35f, waveform = floatArrayOf())))
        advanceUntilIdle()
        val pulse = viewModel.uiState.value.pulseCard
        assertThat(pulse.bpm).isEqualTo(72f)
        assertThat(pulse.isLive).isTrue()
    }

    @Test
    fun `pulse card is amber when rmssd below 20ms`() = runTest {
        val viewModel = vm(ppg(PpgSample(bpm = 80f, rmssd = 15f, waveform = floatArrayOf())))
        advanceUntilIdle()
        val pulse = viewModel.uiState.value.pulseCard
        assertThat(pulse.rmssd).isEqualTo(15f)
    }

    @Test
    fun `neuro card shows stability when tremor sample arrives`() = runTest {
        val sample = TremorSample(dominantFreqHz = 4f, amplitude = 0.1f, fftBins = floatArrayOf())
        val viewModel = vm(tremorProcessor = tremor(sample))
        advanceUntilIdle()
        val neuro = viewModel.uiState.value.neuroCard
        assertThat(neuro.dominantFreqHz).isEqualTo(4f)
        assertThat(neuro.stabilityPercent).isWithin(0.1f).of(90f) // (1-0.1)*100
        assertThat(neuro.isLive).isTrue()
    }

    @Test
    fun `updateVisionResult sets logmar and marks card live`() = runTest {
        val viewModel = vm()
        viewModel.updateVisionResult(0.2f)
        val vision = viewModel.uiState.value.visionCard
        assertThat(vision.logmar).isEqualTo(0.2f)
        assertThat(vision.isLive).isTrue()
    }

    @Test
    fun `updateAudioResult marks card amber when threshold above 30 dBHL`() = runTest {
        val viewModel = vm()
        viewModel.updateAudioResult(45f)
        val audio = viewModel.uiState.value.audioCard
        assertThat(audio.thresholdDbHl).isEqualTo(45f)
        assertThat(audio.isAmber).isTrue()
        assertThat(audio.isLive).isTrue()
    }

    @Test
    fun `updateAudioResult does not flag amber when threshold at or below 30 dBHL`() = runTest {
        val viewModel = vm()
        viewModel.updateAudioResult(25f)
        assertThat(viewModel.uiState.value.audioCard.isAmber).isFalse()
    }

    @Test
    fun `startFullCheckup emits StartFullCheckup event`() = runTest {
        val viewModel = vm()
        viewModel.events.test {
            viewModel.startFullCheckup()
            assertThat(awaitItem()).isInstanceOf(CommandEvent.StartFullCheckup::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
