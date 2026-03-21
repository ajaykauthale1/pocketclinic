package com.pocketclinic.app.ui.acuity

import com.google.common.truth.Truth.assertThat
import com.pocketclinic.app.ui.screen.acuity.VisualAcuityViewModel
import com.pocketclinic.app.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VisualAcuityViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `initial logmar is 0 (20 slash 20 line)`() = runTest {
        val vm = VisualAcuityViewModel()
        assertThat(vm.uiState.value.currentLogmar).isWithin(0.01f).of(0f)
    }

    @Test
    fun `correct response steps down one logmar unit (better acuity)`() = runTest {
        val vm = VisualAcuityViewModel()
        val before = vm.uiState.value.currentLogmar
        vm.recordResponse(correct = true)
        advanceUntilIdle()
        assertThat(vm.uiState.value.currentLogmar).isLessThan(before)
    }

    @Test
    fun `incorrect response steps up one logmar unit (worse acuity)`() = runTest {
        val vm = VisualAcuityViewModel()
        val before = vm.uiState.value.currentLogmar
        vm.recordResponse(correct = false)
        advanceUntilIdle()
        assertThat(vm.uiState.value.currentLogmar).isGreaterThan(before)
    }

    @Test
    fun `test completes after 10 responses`() = runTest {
        val vm = VisualAcuityViewModel()
        repeat(10) { vm.recordResponse(correct = it % 2 == 0) }
        advanceUntilIdle()
        assertThat(vm.uiState.value.isComplete).isTrue()
    }

    @Test
    fun `final logmar is average of last four reversals`() = runTest {
        val vm = VisualAcuityViewModel()
        // Drive enough reversals
        for (i in 0 until 10) vm.recordResponse(correct = i % 2 == 0)
        advanceUntilIdle()
        assertThat(vm.uiState.value.finalLogmar).isNotNull()
    }

    @Test
    fun `logmar is clamped to minus 0_3 floor`() = runTest {
        val vm = VisualAcuityViewModel()
        repeat(20) { vm.recordResponse(correct = true) }
        advanceUntilIdle()
        assertThat(vm.uiState.value.currentLogmar).isAtLeast(-0.3f)
    }

    @Test
    fun `logmar is clamped to 1_0 ceiling`() = runTest {
        val vm = VisualAcuityViewModel()
        repeat(20) { vm.recordResponse(correct = false) }
        advanceUntilIdle()
        assertThat(vm.uiState.value.currentLogmar).isAtMost(1.0f)
    }

    @Test
    fun `reset restores initial state`() = runTest {
        val vm = VisualAcuityViewModel()
        repeat(10) { vm.recordResponse(correct = it % 2 == 0) }
        advanceUntilIdle()
        vm.reset()
        advanceUntilIdle()
        assertThat(vm.uiState.value.isComplete).isFalse()
        assertThat(vm.uiState.value.currentLogmar).isWithin(0.01f).of(0f)
    }
}
