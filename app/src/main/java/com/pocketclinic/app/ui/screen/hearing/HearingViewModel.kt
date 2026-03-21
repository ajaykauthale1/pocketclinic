package com.pocketclinic.app.ui.screen.hearing

import androidx.lifecycle.ViewModel
import com.pocketclinic.app.sensor.hearing.PureToneGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class HearingState(
    val currentFreqHz: Int = 500,   // FREQUENCIES[0]
    val currentDb: Float   = 60f,
    val freqIndex: Int     = 0,
    val results: Map<Int, Float> = emptyMap(),  // freq → threshold dB
    val isComplete: Boolean = false,
)

@HiltViewModel
class HearingViewModel @Inject constructor(
    private val toneGenerator: PureToneGenerator,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HearingState())
    val uiState: StateFlow<HearingState> = _uiState.asStateFlow()

    fun startTone() {
        val s = _uiState.value
        toneGenerator.play(s.currentFreqHz, s.currentDb)
    }

    fun stopTone() {
        toneGenerator.stop()
    }

    /** Record whether the patient heard the tone at the current level. */
    fun recordHeard(heard: Boolean) {
        val step = if (heard) -10f else 10f
        _uiState.update { state ->
            state.copy(currentDb = (state.currentDb + step).coerceIn(DB_MIN, DB_MAX))
        }
    }

    /** Lock in the current frequency's threshold and advance to the next. */
    fun nextFrequency() {
        toneGenerator.stop()
        val state = _uiState.value
        val newResults = state.results + (state.currentFreqHz to state.currentDb)
        val newIndex   = state.freqIndex + 1
        val complete   = newIndex >= FREQUENCIES.size
        _uiState.update {
            it.copy(
                results      = newResults,
                freqIndex    = newIndex,
                currentFreqHz = if (complete) it.currentFreqHz else FREQUENCIES[newIndex],
                currentDb    = INITIAL_DB,
                isComplete   = complete,
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        toneGenerator.stop()
    }

    companion object {
        val FREQUENCIES  = listOf(500, 1000, 2000, 4000, 8000)
        const val INITIAL_DB = 60f
        const val DB_MIN     = 10f
        const val DB_MAX     = 90f
    }
}
