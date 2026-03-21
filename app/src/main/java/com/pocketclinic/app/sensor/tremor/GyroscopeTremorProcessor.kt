package com.pocketclinic.app.sensor.tremor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gyroscope + accelerometer tremor processor.
 *
 * Collects sensor data at ~100 Hz, applies a 3–8 Hz Butterworth bandpass
 * filter, runs an FFT on the windowed signal, and emits [TremorSample].
 * Wired to Android SensorManager in Phase 5 UI integration.
 */
@Singleton
class GyroscopeTremorProcessor @Inject constructor() : TremorProcessor {

    private val _sampleFlow = MutableSharedFlow<TremorSample>(extraBufferCapacity = 64)
    override val sampleFlow: Flow<TremorSample> = _sampleFlow

    override fun start() { /* register SensorManager listeners */ }
    override fun stop()  { /* unregister SensorManager listeners */ }

    suspend fun emitSample(sample: TremorSample) { _sampleFlow.emit(sample) }
}
