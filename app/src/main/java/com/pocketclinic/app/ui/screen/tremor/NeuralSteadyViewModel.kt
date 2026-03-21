package com.pocketclinic.app.ui.screen.tremor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketclinic.app.sensor.tremor.TremorProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NeuralSteadyState(
    val dominantFreqHz: Float? = null,
    val amplitude: Float       = 0f,
    val fftBins: FloatArray    = floatArrayOf(),
    val isRunning: Boolean     = false,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NeuralSteadyState) return false
        return dominantFreqHz == other.dominantFreqHz &&
            amplitude == other.amplitude &&
            fftBins.contentEquals(other.fftBins) &&
            isRunning == other.isRunning
    }
    override fun hashCode(): Int =
        31 * (31 * (31 * (dominantFreqHz?.hashCode() ?: 0) + amplitude.hashCode()) +
            fftBins.contentHashCode()) + isRunning.hashCode()
}

@HiltViewModel
class NeuralSteadyViewModel @Inject constructor(
    private val processor: TremorProcessor,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NeuralSteadyState())
    val uiState: StateFlow<NeuralSteadyState> = _uiState.asStateFlow()

    private var collectionJob: Job? = null

    fun startMeasurement() {
        if (_uiState.value.isRunning) return
        _uiState.update { it.copy(isRunning = true) }
        processor.start()
        collectionJob = viewModelScope.launch {
            processor.sampleFlow.collect { sample ->
                _uiState.update { state ->
                    state.copy(
                        dominantFreqHz = sample.dominantFreqHz,
                        amplitude      = sample.amplitude,
                        fftBins        = sample.fftBins,
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
