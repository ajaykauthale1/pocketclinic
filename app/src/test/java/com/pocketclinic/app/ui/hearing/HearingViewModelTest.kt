package com.pocketclinic.app.ui.hearing

import com.google.common.truth.Truth.assertThat
import com.pocketclinic.app.sensor.hearing.PureToneGenerator
import com.pocketclinic.app.ui.screen.hearing.HearingViewModel
import com.pocketclinic.app.util.MainDispatcherRule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HearingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun vm() = HearingViewModel(mockk(relaxed = true))

    @Test
    fun `initial frequency is 500 Hz`() = runTest {
        assertThat(vm().uiState.value.currentFreqHz).isEqualTo(500)
    }

    @Test
    fun `initial volume is 60 dB`() = runTest {
        assertThat(vm().uiState.value.currentDb).isEqualTo(60f)
    }

    @Test
    fun `heard lowers volume by 10 dB`() = runTest {
        val vm = vm()
        val before = vm.uiState.value.currentDb
        vm.recordHeard(heard = true)
        advanceUntilIdle()
        assertThat(vm.uiState.value.currentDb).isLessThan(before)
    }

    @Test
    fun `not heard raises volume by 10 dB`() = runTest {
        val vm = vm()
        val before = vm.uiState.value.currentDb
        vm.recordHeard(heard = false)
        advanceUntilIdle()
        assertThat(vm.uiState.value.currentDb).isGreaterThan(before)
    }

    @Test
    fun `volume is clamped at 10 dB floor`() = runTest {
        val vm = vm()
        repeat(20) { vm.recordHeard(heard = true) }
        advanceUntilIdle()
        assertThat(vm.uiState.value.currentDb).isAtLeast(10f)
    }

    @Test
    fun `volume is clamped at 90 dB ceiling`() = runTest {
        val vm = vm()
        repeat(20) { vm.recordHeard(heard = false) }
        advanceUntilIdle()
        assertThat(vm.uiState.value.currentDb).isAtMost(90f)
    }

    @Test
    fun `advancing to next frequency moves through standard audiogram sequence`() = runTest {
        val vm = vm()
        val firstFreq = vm.uiState.value.currentFreqHz
        vm.nextFrequency()
        advanceUntilIdle()
        assertThat(vm.uiState.value.currentFreqHz).isNotEqualTo(firstFreq)
    }

    @Test
    fun `test is complete after all standard frequencies are tested`() = runTest {
        val vm = vm()
        // Standard audiogram: 500, 1000, 2000, 4000, 8000 Hz = 5 frequencies
        repeat(5) { vm.nextFrequency() }
        advanceUntilIdle()
        assertThat(vm.uiState.value.isComplete).isTrue()
    }
}
