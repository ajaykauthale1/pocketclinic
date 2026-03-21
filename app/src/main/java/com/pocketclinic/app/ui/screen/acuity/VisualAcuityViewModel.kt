package com.pocketclinic.app.ui.screen.acuity

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class VisualAcuityState(
    val currentLogmar: Float  = 0f,       // current line on ETDRS chart
    val responseCount: Int    = 0,        // total responses given
    val isComplete: Boolean   = false,
    val finalLogmar: Float?   = null,     // average of last 4 reversal points
)

/**
 * Implements a simplified ETDRS staircase procedure.
 *
 * - Each correct response improves acuity by 0.1 LogMAR.
 * - Each incorrect response worsens acuity by 0.1 LogMAR.
 * - Acuity is clamped to [−0.3, 1.0] LogMAR.
 * - The test ends after [MAX_RESPONSES] responses.
 * - Final score = average of the last 4 reversal points (or all reversals if < 4).
 */
@HiltViewModel
class VisualAcuityViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(VisualAcuityState())
    val uiState: StateFlow<VisualAcuityState> = _uiState.asStateFlow()

    private val reversals = mutableListOf<Float>()
    private var lastWasCorrect: Boolean? = null

    fun recordResponse(correct: Boolean) {
        val state = _uiState.value
        if (state.isComplete) return

        val step       = if (correct) -0.1f else 0.1f
        val newLogmar  = (state.currentLogmar + step).coerceIn(-0.3f, 1.0f)
        val newCount   = state.responseCount + 1

        // Detect reversal (direction change)
        val prevCorrect = lastWasCorrect
        if (prevCorrect != null && prevCorrect != correct) {
            reversals.add(state.currentLogmar)
        }
        lastWasCorrect = correct

        val complete = newCount >= MAX_RESPONSES
        val final    = if (complete) computeFinal() else null

        _uiState.update { it.copy(
            currentLogmar = newLogmar,
            responseCount = newCount,
            isComplete    = complete,
            finalLogmar   = final,
        ) }
    }

    fun reset() {
        reversals.clear()
        lastWasCorrect = null
        _uiState.value = VisualAcuityState()
    }

    private fun computeFinal(): Float {
        val samples = reversals.takeLast(4).ifEmpty { listOf(_uiState.value.currentLogmar) }
        return samples.average().toFloat()
    }

    companion object {
        private const val MAX_RESPONSES = 10
    }
}
