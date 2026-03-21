package com.pocketclinic.app.sensor.ppg

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * CameraX-backed PPG processor.
 *
 * Reads YUV_420_888 frames from the front camera, extracts the red-channel
 * mean per frame, applies a Butterworth bandpass filter (0.5–4 Hz), detects
 * R-peaks, and computes BPM + RMSSD.
 *
 * Implementation is wired up in Phase 4 when the CameraX preview is active.
 */
@Singleton
class CameraXPpgProcessor @Inject constructor() : PpgProcessor {

    private val _sampleFlow = MutableSharedFlow<PpgSample>(extraBufferCapacity = 64)
    override val sampleFlow: Flow<PpgSample> = _sampleFlow

    override fun start() {
        // CameraX lifecycle binding happens in VascularPulseScreen via CameraProvider
    }

    override fun stop() {
        // Unbind CameraX use-cases
    }

    /** Called from the CameraX ImageAnalysis use-case on the analysis executor thread. */
    suspend fun emitSample(sample: PpgSample) {
        _sampleFlow.emit(sample)
    }
}
