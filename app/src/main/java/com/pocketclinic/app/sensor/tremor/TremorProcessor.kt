package com.pocketclinic.app.sensor.tremor

import kotlinx.coroutines.flow.Flow

/**
 * Contract for the tremor signal processor.
 *
 * The real implementation reads the gyroscope / accelerometer at 100 Hz,
 * applies a Butterworth bandpass filter (3–8 Hz), runs an FFT, and emits
 * [TremorSample] values with dominant frequency and amplitude.
 */
interface TremorProcessor {
    val sampleFlow: Flow<TremorSample>
    fun start()
    fun stop()
}
