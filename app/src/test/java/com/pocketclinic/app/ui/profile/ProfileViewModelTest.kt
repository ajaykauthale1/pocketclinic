package com.pocketclinic.app.ui.profile

import com.google.common.truth.Truth.assertThat
import com.pocketclinic.app.domain.repository.PatientProfile
import com.pocketclinic.app.domain.repository.ProfileRepository
import com.pocketclinic.app.util.MainDispatcherRule
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun repo(profile: PatientProfile? = null): ProfileRepository =
        mockk(relaxed = true) {
            every { observe() } returns flowOf(profile)
        }

    private fun vm(repo: ProfileRepository = repo()) = ProfileViewModel(repo)

    @Test
    fun `absent profile shows empty setup state`() = runTest {
        val viewModel = vm(repo(null))
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.isSetup).isFalse()
        assertThat(viewModel.uiState.value.name).isEmpty()
    }

    @Test
    fun `existing profile populates state and marks isSetup true`() = runTest {
        val profile = PatientProfile(
            name = "Alex Rivera", dateOfBirth = 0L, gender = "X", updatedAt = 1000L
        )
        val viewModel = vm(repo(profile))
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.isSetup).isTrue()
        assertThat(viewModel.uiState.value.name).isEqualTo("Alex Rivera")
        assertThat(viewModel.uiState.value.savedAt).isEqualTo(1000L)
    }

    @Test
    fun `onNameChanged updates name in state`() = runTest {
        val viewModel = vm()
        viewModel.onNameChanged("Jane Doe")
        assertThat(viewModel.uiState.value.name).isEqualTo("Jane Doe")
    }

    @Test
    fun `onGenderChanged updates gender in state`() = runTest {
        val viewModel = vm()
        viewModel.onGenderChanged("Female")
        assertThat(viewModel.uiState.value.gender).isEqualTo("Female")
    }

    @Test
    fun `onDateOfBirthChanged updates dob in state`() = runTest {
        val viewModel = vm()
        viewModel.onDateOfBirthChanged(631152000000L)
        assertThat(viewModel.uiState.value.dateOfBirth).isEqualTo(631152000000L)
    }

    @Test
    fun `save with blank name does nothing`() = runTest {
        val repository = repo()
        val viewModel = vm(repository)
        viewModel.save()
        advanceUntilIdle()
        coVerify(exactly = 0) { repository.save(any()) }
    }

    @Test
    fun `save with valid name calls repository and sets isSetup true`() = runTest {
        val repository = repo()
        val viewModel = vm(repository)
        viewModel.onNameChanged("Sam")
        viewModel.save()
        advanceUntilIdle()
        coVerify(exactly = 1) { repository.save(any()) }
        assertThat(viewModel.uiState.value.isSetup).isTrue()
    }

    @Test
    fun `save trims whitespace from name`() = runTest {
        val repository = repo()
        val viewModel = vm(repository)
        viewModel.onNameChanged("  Jordan  ")
        viewModel.save()
        advanceUntilIdle()
        coVerify { repository.save(match { it.name == "Jordan" }) }
    }
}
