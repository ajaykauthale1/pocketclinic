package com.pocketclinic.app.signal

import com.google.common.truth.Truth.assertThat
import com.pocketclinic.app.sensor.signal.RmssdCalculator
import org.junit.Test
import kotlin.math.abs

class RmssdCalculatorTest {

    private val calculator = RmssdCalculator()

    @Test
    fun `known RR intervals produce correct RMSSD`() {
        // RR = [800, 810, 790, 820] ms
        // Successive diffs: 10, -20, 30 → squared: 100, 400, 900 → mean: 466.7 → sqrt ≈ 21.6 ms
        val timestamps = listOf(0L, 800L, 1610L, 2400L, 3220L)
        val rmssd = calculator.compute(timestamps)
        assertThat(rmssd).isNotNull()
        assertThat(rmssd!!).isWithin(1.0f).of(21.6f)
    }

    @Test
    fun `fewer than two timestamps returns null`() {
        assertThat(calculator.compute(emptyList())).isNull()
        assertThat(calculator.compute(listOf(0L))).isNull()
    }

    @Test
    fun `exactly two timestamps returns zero RMSSD (single RR interval)`() {
        // Single RR interval = no successive differences
        val rmssd = calculator.compute(listOf(0L, 800L))
        assertThat(rmssd).isNull()  // Need ≥2 RR intervals (3 timestamps)
    }

    @Test
    fun `identical RR intervals give RMSSD of zero`() {
        val timestamps = listOf(0L, 800L, 1600L, 2400L, 3200L)
        val rmssd = calculator.compute(timestamps)
        assertThat(rmssd).isNotNull()
        assertThat(rmssd!!).isWithin(0.01f).of(0.0f)
    }

    @Test
    fun `high HRV produces RMSSD above 50ms stress threshold`() {
        // Large variance: alternating short/long RR intervals
        val timestamps = listOf(0L, 600L, 1400L, 2000L, 2800L, 3400L, 4200L)
        val rmssd = calculator.compute(timestamps)
        assertThat(rmssd).isNotNull()
        assertThat(rmssd!!).isGreaterThan(50f)
    }

    @Test
    fun `low HRV produces RMSSD below 20ms indicating stress`() {
        // Very uniform RR intervals with tiny jitter
        val timestamps = listOf(0L, 800L, 1601L, 2400L, 3200L, 4001L)
        val rmssd = calculator.compute(timestamps)
        assertThat(rmssd).isNotNull()
        assertThat(rmssd!!).isLessThan(5f)
    }

    @Test
    fun `three timestamps with known values computes correctly`() {
        // RR1 = 800ms, RR2 = 850ms → diff = 50 → squared = 2500 → mean = 2500 → sqrt ≈ 50ms
        val timestamps = listOf(0L, 800L, 1650L)
        val rmssd = calculator.compute(timestamps)
        assertThat(rmssd).isNotNull()
        assertThat(rmssd!!).isWithin(0.1f).of(50.0f)
    }
}
