package com.pocketclinic.app.signal

import com.google.common.truth.Truth.assertThat
import com.pocketclinic.app.sensor.signal.PeakDetector
import org.junit.Test
import kotlin.math.PI
import kotlin.math.sin

class PeakDetectorTest {

    private val detector = PeakDetector(
        amplitudeThreshold = 0.3,
        minPeakDistanceSamples = 10,
    )

    @Test
    fun `peaks at known indices are correctly identified`() {
        // Synthetic signal with peaks at index 30, 60, 90
        val signal = DoubleArray(120) { 0.0 }
        signal[30] = 1.0
        signal[60] = 1.0
        signal[90] = 1.0
        val result = detector.detect(signal)
        assertThat(result.peakIndices).containsExactly(30, 60, 90)
    }

    @Test
    fun `flat signal returns empty peak list`() {
        val signal = DoubleArray(120) { 0.5 }
        val result = detector.detect(signal)
        assertThat(result.peakIndices).isEmpty()
    }

    @Test
    fun `noise below threshold is ignored`() {
        val signal = DoubleArray(120) { 0.1 }  // all below 0.3 threshold
        signal[30] = 0.2   // still below threshold
        val result = detector.detect(signal)
        assertThat(result.peakIndices).isEmpty()
    }

    @Test
    fun `single peak signal returns that one index`() {
        val signal = DoubleArray(50) { 0.0 }
        signal[25] = 1.0
        val result = detector.detect(signal)
        assertThat(result.peakIndices).containsExactly(25)
    }

    @Test
    fun `two peaks closer than minDistance returns only one`() {
        val signal = DoubleArray(50) { 0.0 }
        signal[20] = 1.0
        signal[25] = 0.9   // within minPeakDistanceSamples = 10
        val result = detector.detect(signal)
        assertThat(result.peakIndices).hasSize(1)
        assertThat(result.peakIndices.first()).isEqualTo(20)  // first wins
    }

    @Test
    fun `BPM is computed from two peaks 500ms apart at 30fps giving 120 BPM`() {
        // 500ms at 30fps = 15 samples between peaks → 60000/500 = 120 BPM
        val signal = DoubleArray(60) { 0.0 }
        signal[10] = 1.0
        signal[25] = 1.0   // 15 samples apart
        val result = detector.detect(signal, sampleRateHz = 30.0)
        assertThat(result.instantaneousBpm).isWithin(1f).of(120f)
    }

    @Test
    fun `single peak returns null BPM`() {
        val signal = DoubleArray(60) { 0.0 }
        signal[30] = 1.0
        val result = detector.detect(signal, sampleRateHz = 30.0)
        assertThat(result.instantaneousBpm).isNull()
    }

    @Test
    fun `empty signal returns empty result without exception`() {
        val result = detector.detect(DoubleArray(0))
        assertThat(result.peakIndices).isEmpty()
        assertThat(result.instantaneousBpm).isNull()
    }

    @Test
    fun `PPG-like sine wave at 1Hz has peaks approximately 30 samples apart at 30fps`() {
        val sampleRate = 30.0
        val n = 180  // 6 seconds
        val signal = DoubleArray(n) { i -> sin(2 * PI * 1.0 * i / sampleRate) }
        val result = detector.detect(signal, sampleRateHz = sampleRate)
        // Expect ~5 peaks in 6 seconds for 1 Hz signal = 60 BPM
        assertThat(result.peakIndices.size).isAtLeast(4)
        result.instantaneousBpm?.let { bpm ->
            assertThat(bpm).isWithin(5f).of(60f)
        }
    }
}
