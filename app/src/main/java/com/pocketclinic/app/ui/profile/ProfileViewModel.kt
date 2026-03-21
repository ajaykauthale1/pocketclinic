package com.pocketclinic.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketclinic.app.domain.repository.PatientProfile
import com.pocketclinic.app.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val name: String        = "",
    val dateOfBirth: Long   = 0L,   // epoch ms; 0 = not set
    val gender: String      = "",
    val isSetup: Boolean    = false, // true once a profile row exists in DB
    val isSaving: Boolean   = false,
    val savedAt: Long?      = null,  // last successful save timestamp
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observe()
                .catch { /* DB error — keep empty state */ }
                .collect { profile ->
                    if (profile != null) {
                        _uiState.update {
                            it.copy(
                                name        = profile.name,
                                dateOfBirth = profile.dateOfBirth,
                                gender      = profile.gender,
                                isSetup     = true,
                                savedAt     = profile.updatedAt,
                            )
                        }
                    }
                }
        }
    }

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onDateOfBirthChanged(epochMs: Long) {
        _uiState.update { it.copy(dateOfBirth = epochMs) }
    }

    fun onGenderChanged(gender: String) {
        _uiState.update { it.copy(gender = gender) }
    }

    fun save() {
        val state = _uiState.value
        if (state.name.isBlank()) return
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            repository.save(
                PatientProfile(
                    name        = state.name.trim(),
                    dateOfBirth = state.dateOfBirth,
                    gender      = state.gender,
                    updatedAt   = now,
                )
            )
            _uiState.update { it.copy(isSaving = false, isSetup = true, savedAt = now) }
        }
    }
}
