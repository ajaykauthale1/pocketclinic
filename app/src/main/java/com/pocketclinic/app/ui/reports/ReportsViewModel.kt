package com.pocketclinic.app.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketclinic.app.domain.model.Session
import com.pocketclinic.app.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class SessionSummary(
    val id: Long,
    val startedAt: Long,           // epoch ms
    val durationMs: Long,
    val label: String,
    val flagged: Boolean,          // true → amber [ ▲ ] shape
)

data class ReportsUiState(
    val sessions: List<SessionSummary> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

sealed interface ReportsEvent {
    data object ExportRequested : ReportsEvent
    data class NavigateToDeepDive(val sessionId: Long) : ReportsEvent
}

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val repository: SessionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    private val _events = Channel<ReportsEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val thirtyDaysMs = TimeUnit.DAYS.toMillis(30)

    init {
        observeSessions()
    }

    private fun observeSessions() {
        viewModelScope.launch {
            repository.observeAll()
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { sessions ->
                    val cutoff = System.currentTimeMillis() - thirtyDaysMs
                    val summaries = sessions
                        .filter { it.startedAt >= cutoff }
                        .sortedByDescending { it.startedAt }
                        .map(Session::toSummary)
                    _uiState.update { it.copy(sessions = summaries, isLoading = false) }
                }
        }
    }

    fun onExportClicked() {
        viewModelScope.launch { _events.send(ReportsEvent.ExportRequested) }
    }

    fun onSessionClicked(sessionId: Long) {
        viewModelScope.launch { _events.send(ReportsEvent.NavigateToDeepDive(sessionId)) }
    }
}

private fun Session.toSummary() = SessionSummary(
    id         = id,
    startedAt  = startedAt,
    durationMs = (finishedAt - startedAt).coerceAtLeast(0L),
    label      = notes.ifBlank { "CHECKUP" },
    flagged    = notes.contains("FLAG", ignoreCase = true),
)
