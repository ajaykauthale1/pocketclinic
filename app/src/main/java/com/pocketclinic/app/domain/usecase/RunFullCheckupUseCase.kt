package com.pocketclinic.app.domain.usecase

import com.pocketclinic.app.domain.model.Session
import com.pocketclinic.app.domain.repository.SessionRepository
import com.pocketclinic.app.sensor.ppg.PpgProcessor
import com.pocketclinic.app.sensor.tremor.TremorProcessor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

enum class TestType { VISION, PULSE, TREMOR, HEARING }

sealed interface CheckupProgress {
    data object Calibrating : CheckupProgress
    data class RunningTest(val type: TestType) : CheckupProgress
    data class TestSkipped(val type: TestType, val reason: String) : CheckupProgress
    data class TestComplete(val type: TestType, val success: Boolean) : CheckupProgress
    data class CheckupDone(val sessionId: Long) : CheckupProgress
}

class RunFullCheckupUseCase @Inject constructor(
    private val ppgProcessor: PpgProcessor,
    private val tremorProcessor: TremorProcessor,
    private val sessionRepository: SessionRepository,
) {
    /**
     * Runs a full clinical checkup — Vision → Pulse → Tremor → Hearing — sequentially.
     *
     * Each test failure is non-blocking; the run always continues to the next test.
     * Emits [CheckupProgress] events for the UI to reflect.
     */
    fun run(): Flow<CheckupProgress> = flow {
        val startedAt = System.currentTimeMillis()

        emit(CheckupProgress.Calibrating)
        delay(CALIBRATION_DELAY_MS)

        // Vision — no hardware sensor required, UI-driven staircase
        emit(CheckupProgress.RunningTest(TestType.VISION))
        val visionOk = runCatching { delay(TEST_DURATION_MS) }.isSuccess
        emit(CheckupProgress.TestComplete(TestType.VISION, visionOk))

        // Pulse — PPG processor
        emit(CheckupProgress.RunningTest(TestType.PULSE))
        val pulseOk = runCatching {
            ppgProcessor.start()
            ppgProcessor.sampleFlow
                .catch { throw it }
                .first()                    // wait for first valid sample
            ppgProcessor.stop()
        }.isSuccess
        if (!pulseOk) ppgProcessor.stop()
        emit(CheckupProgress.TestComplete(TestType.PULSE, pulseOk))

        // Tremor — gyroscope processor
        emit(CheckupProgress.RunningTest(TestType.TREMOR))
        val tremorOk = runCatching {
            tremorProcessor.start()
            tremorProcessor.sampleFlow
                .catch { throw it }
                .first()
            tremorProcessor.stop()
        }.isSuccess
        if (!tremorOk) tremorProcessor.stop()
        emit(CheckupProgress.TestComplete(TestType.TREMOR, tremorOk))

        // Hearing — audio staircase, no hardware check needed at this layer
        emit(CheckupProgress.RunningTest(TestType.HEARING))
        val hearingOk = runCatching { delay(TEST_DURATION_MS) }.isSuccess
        emit(CheckupProgress.TestComplete(TestType.HEARING, hearingOk))

        // Persist the session
        val finishedAt = System.currentTimeMillis()
        val sessionId = sessionRepository.save(
            Session(
                id         = 0,
                startedAt  = startedAt,
                finishedAt = finishedAt,
                notes      = "FULL_CHECKUP",
            )
        )

        emit(CheckupProgress.CheckupDone(sessionId))
    }

    companion object {
        const val CALIBRATION_DELAY_MS = 2_000L
        const val TEST_DURATION_MS     = 500L   // minimum per test in real flow
    }
}
