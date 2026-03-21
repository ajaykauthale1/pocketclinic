package com.pocketclinic.app.sensor.ppg

import kotlinx.coroutines.flow.Flow

/**
 * Contract for the PPG (photoplethysmography) signal processor.
 *
 * The real implementation uses CameraX YUV_420_888 frames to extract the
 * red-channel mean, bandpass-filter it, detect R-peaks, and compute BPM + RMSSD.
 * A fake/mock implementation is used in unit tests.
 */
interface PpgProcessor {
    /** Continuous stream of processed PPG samples. */
    val sampleFlow: Flow<PpgSample>

    fun start()
    fun stop()
}
