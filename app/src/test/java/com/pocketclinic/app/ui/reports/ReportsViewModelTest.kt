package com.pocketclinic.app.ui.reports

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.pocketclinic.app.domain.model.Session
import com.pocketclinic.app.domain.repository.SessionRepository
import com.pocketclinic.app.util.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class ReportsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val now = System.currentTimeMillis()
    private val yesterday = now - TimeUnit.DAYS.toMillis(1)
    private val thirtyOneDaysAgo = now - TimeUnit.DAYS.toMillis(31)

    private fun repo(vararg sessions: Session): SessionRepository =
        mockk { every { observeAll() } returns flowOf(sessions.toList()) }

    private fun session(
        id: Long,
        startedAt: Long = yesterday,
        finishedAt: Long = startedAt + 60_000L,
        notes: String = "",
    ) = Session(id = id, startedAt = startedAt, finishedAt = finishedAt, notes = notes)

    @Test
    fun `initial state is loading`() = runTest {
        val vm = ReportsViewModel(repo())
        // Loading starts true; after collecting it becomes false
        advanceUntilIdle()
        assertThat(vm.uiState.value.isLoading).isFalse()
    }

    @Test
    fun `sessions are sorted newest first`() = runTest {
        val older = session(id = 1, startedAt = yesterday - 3_600_000L)
        val newer = session(id = 2, startedAt = yesterday)
        val vm = ReportsViewModel(repo(older, newer))
        advanceUntilIdle()
        val ids = vm.uiState.value.sessions.map { it.id }
        assertThat(ids).containsExactly(2L, 1L).inOrder()
    }

    @Test
    fun `sessions older than 30 days are excluded`() = runTest {
        val old = session(id = 1, startedAt = thirtyOneDaysAgo)
        val recent = session(id = 2, startedAt = yesterday)
        val vm = ReportsViewModel(repo(old, recent))
        advanceUntilIdle()
        assertThat(vm.uiState.value.sessions.map { it.id }).containsExactly(2L)
    }

    @Test
    fun `session with FLAG in notes is marked flagged`() = runTest {
        val flagged = session(id = 1, notes = "FLAG anomaly detected")
        val vm = ReportsViewModel(repo(flagged))
        advanceUntilIdle()
        assertThat(vm.uiState.value.sessions.first().flagged).isTrue()
    }

    @Test
    fun `session without FLAG in notes is not flagged`() = runTest {
        val normal = session(id = 1, notes = "routine checkup")
        val vm = ReportsViewModel(repo(normal))
        advanceUntilIdle()
        assertThat(vm.uiState.value.sessions.first().flagged).isFalse()
    }

    @Test
    fun `empty repository yields empty sessions list`() = runTest {
        val vm = ReportsViewModel(repo())
        advanceUntilIdle()
        assertThat(vm.uiState.value.sessions).isEmpty()
    }

    @Test
    fun `onExportClicked emits ExportRequested event`() = runTest {
        val vm = ReportsViewModel(repo())
        vm.events.test {
            vm.onExportClicked()
            assertThat(awaitItem()).isInstanceOf(ReportsEvent.ExportRequested::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSessionClicked emits NavigateToDeepDive with correct id`() = runTest {
        val vm = ReportsViewModel(repo(session(id = 42L)))
        vm.events.test {
            vm.onSessionClicked(42L)
            val event = awaitItem()
            assertThat(event).isInstanceOf(ReportsEvent.NavigateToDeepDive::class.java)
            assertThat((event as ReportsEvent.NavigateToDeepDive).sessionId).isEqualTo(42L)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
