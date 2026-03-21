package com.pocketclinic.app.sensor.signal

/**
 * Result of peak detection.
 *
 * @param peakIndices         Indices in the signal where peaks were found.
 * @param instantaneousBpm    BPM computed from the last two detected peaks; null if < 2 peaks.
 */
data class PeakResult(
    val peakIndices: List<Int>,
    val instantaneousBpm: Float?,
)

/**
 * Sliding-window local-maximum peak detector.
 *
 * A sample qualifies as a peak when:
 *   1. Its value exceeds [amplitudeThreshold].
 *   2. It is greater than all neighbours within a local window.
 *   3. It is at least [minPeakDistanceSamples] away from the last accepted peak.
 *
 * @param amplitudeThreshold     Minimum amplitude for a sample to be considered a peak.
 * @param minPeakDistanceSamples Minimum number of samples between successive peaks.
 */
class PeakDetector(
    private val amplitudeThreshold: Double = 0.3,
    private val minPeakDistanceSamples: Int = 10,
) {
    /**
     * Detect peaks in [signal]. Optionally provide [sampleRateHz] to compute [PeakResult.instantaneousBpm].
     */
    fun detect(signal: DoubleArray, sampleRateHz: Double = 30.0): PeakResult {
        if (signal.isEmpty()) return PeakResult(emptyList(), null)

        val peaks       = mutableListOf<Int>()
        var lastPeakIdx = -minPeakDistanceSamples  // allow peak at index 0

        val windowHalf = (minPeakDistanceSamples / 2).coerceAtLeast(1)

        for (i in signal.indices) {
            val value = signal[i]

            // Amplitude gate
            if (value < amplitudeThreshold) continue

            // Local maximum check
            val windowStart = (i - windowHalf).coerceAtLeast(0)
            val windowEnd   = (i + windowHalf).coerceAtMost(signal.size - 1)
            val isLocalMax  = (windowStart..windowEnd).all { j -> j == i || signal[j] < value }
            if (!isLocalMax) continue

            // Minimum distance gate
            if (i - lastPeakIdx < minPeakDistanceSamples) continue

            peaks.add(i)
            lastPeakIdx = i
        }

        val bpm = if (peaks.size >= 2) {
            val lastTwo = peaks.takeLast(2)
            val intervalSamples = lastTwo[1] - lastTwo[0]
            val intervalSeconds = intervalSamples / sampleRateHz
            (60.0 / intervalSeconds).toFloat()
        } else {
            null
        }

        return PeakResult(peakIndices = peaks, instantaneousBpm = bpm)
    }
}
