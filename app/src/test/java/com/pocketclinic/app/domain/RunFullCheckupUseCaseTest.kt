package com.pocketclinic.app.domain

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.pocketclinic.app.domain.model.Session
import com.pocketclinic.app.domain.repository.SessionRepository
import com.pocketclinic.app.domain.usecase.CheckupProgress
import com.pocketclinic.app.domain.usecase.RunFullCheckupUseCase
import com.pocketclinic.app.domain.usecase.TestType
import com.pocketclinic.app.sensor.ppg.PpgProcessor
import com.pocketclinic.app.sensor.ppg.PpgSample
import com.pocketclinic.app.sensor.tremor.TremorProcessor
import com.pocketclinic.app.sensor.tremor.TremorSample
import com.pocketclinic.app.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RunFullCheckupUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val ppgSample    = PpgSample(72f, 35f, floatArrayOf())
    private val tremorSample = TremorSample(3f, 0.1f, floatArrayOf())

    private fun ppg(sample: PpgSample = ppgSample): PpgProcessor =
        mockk(relaxed = true) { every { sampleFlow } returns flowOf(sample) }

    private fun tremor(sample: TremorSample = tremorSample): TremorProcessor =
        mockk(relaxed = true) { every { sampleFlow } returns flowOf(sample) }

    private fun repo(savedId: Long = 1L): SessionRepository =
        mockk(relaxed = true) { coEvery { save(any()) } returns savedId }

    private fun useCase(
        ppgProcessor: PpgProcessor     = ppg(),
        tremorProcessor: TremorProcessor = tremor(),
        sessionRepository: SessionRepository = repo(),
    ) = RunFullCheckupUseCase(ppgProcessor, tremorProcessor, sessionRepository)

    @Test
    fun `first event emitted is Calibrating`() = runTest {
        useCase().run().test {
            assertThat(awaitItem()).isInstanceOf(CheckupProgress.Calibrating::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `RunningTest events emitted for all four test types`() = runTest {
        val runningTypes = mutableListOf<TestType>()
        useCase().run().test {
            while (true) {
                val item = awaitItem()
                if (item is CheckupProgress.RunningTest) runningTypes += item.type
                if (item is CheckupProgress.CheckupDone) break
            }
            cancelAndIgnoreRemainingEvents()
        }
        assertThat(runningTypes).containsExactly(
            TestType.VISION, TestType.PULSE, TestType.TREMOR, TestType.HEARING
        ).inOrder()
    }

    @Test
    fun `TestComplete events emitted for all four test types`() = runTest {
        val completedTypes = mutableListOf<TestType>()
        useCase().run().test {
            while (true) {
                val item = awaitItem()
                if (item is CheckupProgress.TestComplete) completedTypes += item.type
                if (item is CheckupProgress.CheckupDone) break
            }
            cancelAndIgnoreRemainingEvents()
        }
        assertThat(completedTypes).containsExactly(
            TestType.VISION, TestType.PULSE, TestType.TREMOR, TestType.HEARING
        ).inOrder()
    }

    @Test
    fun `last event is CheckupDone with saved session id`() = runTest {
        val repository = repo(savedId = 99L)
        var last: CheckupProgress? = null
        useCase(sessionRepository = repository).run().test {
            while (true) {
                val item = awaitItem()
                last = item
                if (item is CheckupProgress.CheckupDone) break
            }
            cancelAndIgnoreRemainingEvents()
        }
        assertThat(last).isInstanceOf(CheckupProgress.CheckupDone::class.java)
        assertThat((last as CheckupProgress.CheckupDone).sessionId).isEqualTo(99L)
    }

    @Test
    fun `pulse sensor failure does not abort run — remaining tests still emit`() = runTest {
        // PPG flow that throws immediately
        val brokenPpg: PpgProcessor = mockk(relaxed = true) {
            every { sampleFlow } returns kotlinx.coroutines.flow.flow { throw RuntimeException("no camera") }
        }
        val completedTypes = mutableListOf<TestType>()
        useCase(ppgProcessor = brokenPpg).run().test {
            while (true) {
                val item = awaitItem()
                if (item is CheckupProgress.TestComplete) completedTypes += item.type
                if (item is CheckupProgress.CheckupDone) break
            }
            cancelAndIgnoreRemainingEvents()
        }
        // All four must still complete
        assertThat(completedTypes).containsExactly(
            TestType.VISION, TestType.PULSE, TestType.TREMOR, TestType.HEARING
        ).inOrder()
    }

    @Test
    fun `session is saved with FULL_CHECKUP notes`() = runTest {
        val repository = repo()
        useCase(sessionRepository = repository).run().test {
            while (awaitItem() !is CheckupProgress.CheckupDone) { /* drain */ }
            cancelAndIgnoreRemainingEvents()
        }
        io.mockk.coVerify {
            repository.save(match { it.notes == "FULL_CHECKUP" })
        }
    }

    @Test
    fun `tremor sensor failure is non-blocking`() = runTest {
        val brokenTremor: TremorProcessor = mockk(relaxed = true) {
            every { sampleFlow } returns kotlinx.coroutines.flow.flow { throw RuntimeException("no gyro") }
        }
        var done = false
        useCase(tremorProcessor = brokenTremor).run().test {
            while (true) {
                if (awaitItem() is CheckupProgress.CheckupDone) { done = true; break }
            }
            cancelAndIgnoreRemainingEvents()
        }
        assertThat(done).isTrue()
    }
}
