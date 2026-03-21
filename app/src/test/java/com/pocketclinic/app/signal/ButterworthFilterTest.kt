package com.pocketclinic.app.signal

import com.google.common.truth.Truth.assertThat
import com.pocketclinic.app.sensor.signal.ButterworthFilter
import com.pocketclinic.app.sensor.signal.FilterConfig
import org.junit.Test
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

class ButterworthFilterTest {

    // PPG bandpass: 0.5–4 Hz at 30 fps
    private val ppgConfig = FilterConfig(lowCutHz = 0.5, highCutHz = 4.0, sampleRateHz = 30.0, order = 4)

    @Test
    fun `DC signal is attenuated to near zero by bandpass filter`() {
        val filter = ButterworthFilter(ppgConfig)
        val dc = DoubleArray(256) { 1.0 }           // constant offset (DC)
        val out = filter.filter(dc)
        // After transient, DC should be near zero — check last 128 samples
        val tail = out.takeLast(128)
        tail.forEach { assertThat(abs(it)).isLessThan(0.05) }
    }

    @Test
    fun `1Hz sine wave passes through PPG bandpass with minimal attenuation`() {
        val filter = ButterworthFilter(ppgConfig)
        val sampleRate = 30.0
        val n = 256
        // 1 Hz sine at 30 fps
        val signal = DoubleArray(n) { i -> sin(2 * PI * 1.0 * i / sampleRate) }
        val out = filter.filter(signal)
        // Check RMS of last 64 samples — should retain significant amplitude
        val rms = out.takeLast(64).map { it * it }.average().let { Math.sqrt(it) }
        assertThat(rms).isGreaterThan(0.5)  // < 3dB attenuation → amplitude > 0.707 of 1.0
    }

    @Test
    fun `10Hz sine wave is heavily attenuated outside PPG bandpass`() {
        val filter = ButterworthFilter(ppgConfig)
        val sampleRate = 30.0
        val n = 256
        val signal = DoubleArray(n) { i -> sin(2 * PI * 10.0 * i / sampleRate) }
        val out = filter.filter(signal)
        val rms = out.takeLast(64).map { it * it }.average().let { Math.sqrt(it) }
        assertThat(rms).isLessThan(0.1)  // heavily attenuated
    }

    @Test
    fun `empty input returns empty DoubleArray without exception`() {
        val filter = ButterworthFilter(ppgConfig)
        val out = filter.filter(DoubleArray(0))
        assertThat(out).isEmpty()
    }

    @Test
    fun `single sample input returns without exception`() {
        val filter = ButterworthFilter(ppgConfig)
        val out = filter.filter(doubleArrayOf(0.5))
        assertThat(out).hasLength(1)
    }

    @Test
    fun `tremor bandpass 3 to 8 Hz at 100 Hz sample rate passes 5Hz signal`() {
        val tremorConfig = FilterConfig(lowCutHz = 3.0, highCutHz = 8.0, sampleRateHz = 100.0, order = 4)
        val filter = ButterworthFilter(tremorConfig)
        val n = 512
        val signal = DoubleArray(n) { i -> sin(2 * PI * 5.0 * i / 100.0) }
        val out = filter.filter(signal)
        val rms = out.takeLast(128).map { it * it }.average().let { Math.sqrt(it) }
        assertThat(rms).isGreaterThan(0.4)
    }

    @Test
    fun `filtering 256 samples completes within one PPG frame budget of 16ms`() {
        val filter = ButterworthFilter(ppgConfig)
        val signal = DoubleArray(256) { i -> sin(2 * PI * 1.0 * i / 30.0) }
        // Warm up JIT
        repeat(5) { filter.filter(signal) }
        val start = System.nanoTime()
        repeat(10) { filter.filter(signal) }
        val avgMs = (System.nanoTime() - start) / 10.0 / 1_000_000.0
        assertThat(avgMs).isLessThan(16.0)
    }

    @Test
    fun `filter state is independent between separate instances`() {
        val filter1 = ButterworthFilter(ppgConfig)
        val filter2 = ButterworthFilter(ppgConfig)
        val signal = DoubleArray(64) { i -> sin(2 * PI * 1.0 * i / 30.0) }
        val out1 = filter1.filter(signal)
        val out2 = filter2.filter(signal)
        // Both fresh instances should produce identical output
        out1.zip(out2).forEach { (a, b) -> assertThat(abs(a - b)).isLessThan(1e-10) }
    }
}
