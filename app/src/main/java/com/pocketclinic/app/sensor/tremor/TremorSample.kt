package com.pocketclinic.app.sensor.tremor

/**
 * A processed tremor measurement emitted by [TremorProcessor].
 *
 * @param dominantFreqHz Dominant tremor frequency in Hz (typically 3–8 Hz for Parkinsonism).
 * @param amplitude      Normalised RMS amplitude of the tremor signal.
 * @param fftBins        Magnitude spectrum bins for visualisation.
 */
data class TremorSample(
    val dominantFreqHz: Float,
    val amplitude: Float,
    val fftBins: FloatArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TremorSample) return false
        return dominantFreqHz == other.dominantFreqHz &&
            amplitude == other.amplitude &&
            fftBins.contentEquals(other.fftBins)
    }
    override fun hashCode(): Int =
        31 * (31 * dominantFreqHz.hashCode() + amplitude.hashCode()) + fftBins.contentHashCode()
}
