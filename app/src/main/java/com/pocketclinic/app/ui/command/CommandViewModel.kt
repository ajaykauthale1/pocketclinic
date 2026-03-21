package com.pocketclinic.app.ui.command

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketclinic.app.sensor.ppg.PpgProcessor
import com.pocketclinic.app.sensor.tremor.TremorProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PulseCardState(
    val bpm: Float? = null,
    val rmssd: Float? = null,
    val isLive: Boolean = false,
)

data class NeuroCardState(
    val dominantFreqHz: Float? = null,
    val stabilityPercent: Float? = null,
    val isLive: Boolean = false,
)

data class VisionCardState(
    val logmar: Float? = null,
    val isLive: Boolean = false,
)

data class AudioCardState(
    val thresholdDbHl: Float? = null,
    val isAmber: Boolean = false,
    val isLive: Boolean = false,
)

data class CommandUiState(
    val pulseCard: PulseCardState = PulseCardState(),
    val neuroCard: NeuroCardState = NeuroCardState(),
    val visionCard: VisionCardState = VisionCardState(),
    val audioCard: AudioCardState = AudioCardState(),
)

sealed interface CommandEvent {
    data object StartFullCheckup : CommandEvent
}

@HiltViewModel
class CommandViewModel @Inject constructor(
    private val ppgProcessor: PpgProcessor,
    private val tremorProcessor: TremorProcessor,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommandUiState())
    val uiState: StateFlow<CommandUiState> = _uiState.asStateFlow()

    private val _events = Channel<CommandEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        collectPpg()
        collectTremor()
    }

    private fun collectPpg() {
        viewModelScope.launch {
            ppgProcessor.sampleFlow
                .catch { /* sensor unavailable — keep card idle */ }
                .collect { sample ->
                    _uiState.update { state ->
                        state.copy(
                            pulseCard = state.pulseCard.copy(
                                bpm    = sample.bpm ?: state.pulseCard.bpm,
                                rmssd  = sample.rmssd ?: state.pulseCard.rmssd,
                                isLive = true,
                            )
                        )
                    }
                }
        }
    }

    private fun collectTremor() {
        viewModelScope.launch {
            tremorProcessor.sampleFlow
                .catch { /* sensor unavailable — keep card idle */ }
                .collect { sample ->
                    val stabilityPct = ((1f - sample.amplitude) * 100f).coerceIn(0f, 100f)
                    _uiState.update { state ->
                        state.copy(
                            neuroCard = state.neuroCard.copy(
                                dominantFreqHz  = sample.dominantFreqHz,
                                stabilityPercent = stabilityPct,
                                isLive          = true,
                            )
                        )
                    }
                }
        }
    }

    /** Update vision card with a completed acuity result. */
    fun updateVisionResult(logmar: Float) {
        _uiState.update { it.copy(visionCard = VisionCardState(logmar = logmar, isLive = true)) }
    }

    /** Update audio card with a completed hearing threshold result. */
    fun updateAudioResult(thresholdDbHl: Float) {
        _uiState.update {
            it.copy(
                audioCard = AudioCardState(
                    thresholdDbHl = thresholdDbHl,
                    isAmber       = thresholdDbHl > 30f,
                    isLive        = true,
                )
            )
        }
    }

    fun startFullCheckup() {
        viewModelScope.launch { _events.send(CommandEvent.StartFullCheckup) }
    }
}
