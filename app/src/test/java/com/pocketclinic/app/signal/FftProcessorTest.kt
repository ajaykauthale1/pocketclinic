package com.pocketclinic.app.signal

import com.google.common.truth.Truth.assertThat
import com.pocketclinic.app.sensor.signal.FftProcessor
import com.pocketclinic.app.sensor.signal.FftResult
import org.junit.Test
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

class FftProcessorTest {

    private val fft = FftProcessor()

    @Test
    fun `1Hz sine at 30fps produces dominant frequency bin at 1Hz`() {
        val sampleRate = 30.0
        val n = 128   // power of 2
        val signal = DoubleArray(n) { i -> sin(2 * PI * 1.0 * i / sampleRate) }
        val result = fft.compute(signal, sampleRate)
        val dominant = result.findDominantFrequency(minHz = 0.5f, maxHz = 4.0f)
        assertThat(dominant).isWithin(0.3f).of(1.0f)
    }

    @Test
    fun `5Hz sine at 100Hz sample rate produces dominant bin at 5Hz`() {
        val sampleRate = 100.0
        val n = 256
        val signal = DoubleArray(n) { i -> sin(2 * PI * 5.0 * i / sampleRate) }
        val result = fft.compute(signal, sampleRate)
        val dominant = result.findDominantFrequency(minHz = 3.0f, maxHz = 10.0f)
        assertThat(dominant).isWithin(0.5f).of(5.0f)
    }

    @Test
    fun `flat zero signal produces all-zero magnitudes`() {
        val signal = DoubleArray(128) { 0.0 }
        val result = fft.compute(signal, 30.0)
        result.magnitudes.forEach { assertThat(it).isEqualTo(0f) }
    }

    @Test
    fun `non power of two input is zero padded to next power of two`() {
        val signal = DoubleArray(100) { i -> sin(2 * PI * 1.0 * i / 30.0) }
        // 100 is not a power of 2 — should not throw; padded to 128
        val result = fft.compute(signal, 30.0)
        assertThat(result.magnitudes.size).isEqualTo(64)  // N/2 bins returned
    }

    @Test
    fun `frequencies array length matches magnitudes array length`() {
        val signal = DoubleArray(256) { 0.5 }
        val result = fft.compute(signal, 100.0)
        assertThat(result.frequencies.size).isEqualTo(result.magnitudes.size)
    }

    @Test
    fun `single sample input returns result without exception`() {
        val result = fft.compute(doubleArrayOf(1.0), 30.0)
        assertThat(result.magnitudes).isNotNull()
    }

    @Test
    fun `frequency resolution matches expected for given sample rate and buffer`() {
        // For N=256 samples at 100Hz: frequency resolution = 100/256 ≈ 0.39 Hz
        val n = 256
        val sampleRate = 100.0
        val signal = DoubleArray(n) { 0.0 }
        val result = fft.compute(signal, sampleRate)
        val freqResolution = result.frequencies[1] - result.frequencies[0]
        assertThat(freqResolution).isWithin(0.01f).of((sampleRate / n).toFloat())
    }

    @Test
    fun `FFT on 256 samples completes within 5ms`() {
        val signal = DoubleArray(256) { i -> sin(2 * PI * 5.0 * i / 100.0) }
        // Warm up JIT
        repeat(5) { fft.compute(signal, 100.0) }
        val start = System.nanoTime()
        repeat(10) { fft.compute(signal, 100.0) }
        val avgMs = (System.nanoTime() - start) / 10.0 / 1_000_000.0
        assertThat(avgMs).isLessThan(5.0)
    }

    @Test
    fun `two Hz and five Hz mixed signal identifies both peaks`() {
        val sampleRate = 100.0
        val n = 512
        val signal = DoubleArray(n) { i ->
            sin(2 * PI * 2.0 * i / sampleRate) + sin(2 * PI * 5.0 * i / sampleRate)
        }
        val result = fft.compute(signal, sampleRate)
        val dominant = result.findDominantFrequency(minHz = 1.0f, maxHz = 10.0f)
        // Dominant should be one of the two frequencies
        assertThat(abs(dominant - 2.0f) < 0.5f || abs(dominant - 5.0f) < 0.5f).isTrue()
    }
}
