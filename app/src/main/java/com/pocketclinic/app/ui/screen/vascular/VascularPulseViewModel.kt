package com.pocketclinic.app.ui.screen.vascular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketclinic.app.sensor.ppg.PpgProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VascularPulseState(
    val bpm: Float?          = null,
    val rmssd: Float?        = null,
    val waveform: List<Float> = emptyList(),
    val isRunning: Boolean   = false,
)

@HiltViewModel
class VascularPulseViewModel @Inject constructor(
    private val processor: PpgProcessor,
) : ViewModel() {

    private val _uiState = MutableStateFlow(VascularPulseState())
    val uiState: StateFlow<VascularPulseState> = _uiState.asStateFlow()

    private var collectionJob: Job? = null

    fun startMeasurement() {
        if (_uiState.value.isRunning) return
        _uiState.update { it.copy(isRunning = true) }
        processor.start()
        collectionJob = viewModelScope.launch {
            processor.sampleFlow.collect { sample ->
                _uiState.update { state ->
                    state.copy(
                        bpm      = sample.bpm ?: state.bpm,
                        rmssd    = sample.rmssd ?: state.rmssd,
                        waveform = sample.waveform.toList(),
                    )
                }
            }
        }
    }

    fun stopMeasurement() {
        collectionJob?.cancel()
        collectionJob = null
        processor.stop()
        _uiState.update { it.copy(isRunning = false) }
    }

    override fun onCleared() {
        super.onCleared()
        stopMeasurement()
    }
}
