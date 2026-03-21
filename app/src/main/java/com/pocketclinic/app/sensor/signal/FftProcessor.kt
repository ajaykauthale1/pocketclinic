package com.pocketclinic.app.sensor.signal

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.log2
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Result of an FFT computation.
 *
 * @param frequencies Frequency of each bin in Hz.
 * @param magnitudes  Magnitude of each bin (0 to N/2).
 */
data class FftResult(
    val frequencies: FloatArray,
    val magnitudes: FloatArray,
) {
    /**
     * Returns the dominant frequency (Hz) within [minHz]..[maxHz].
     * Returns 0f if no bins fall in the range.
     */
    fun findDominantFrequency(minHz: Float = 0f, maxHz: Float = Float.MAX_VALUE): Float {
        var maxMag   = -1f
        var bestFreq = 0f
        for (i in frequencies.indices) {
            val f = frequencies[i]
            if (f < minHz || f > maxHz) continue
            if (magnitudes[i] > maxMag) {
                maxMag   = magnitudes[i]
                bestFreq = f
            }
        }
        return bestFreq
    }
}

/**
 * Pure-Kotlin Cooley-Tukey radix-2 FFT processor.
 *
 * - Input is zero-padded to the next power of two if needed.
 * - Returns only the positive-frequency half (DC to Nyquist).
 * - Thread-safe: stateless, all working buffers are local.
 */
class FftProcessor {

    /** Compute the magnitude spectrum of [signal] sampled at [sampleRateHz]. */
    fun compute(signal: DoubleArray, sampleRateHz: Double): FftResult {
        if (signal.isEmpty()) return FftResult(FloatArray(0), FloatArray(0))

        val n       = nextPowerOfTwo(signal.size)
        val re      = DoubleArray(n) { if (it < signal.size) signal[it] else 0.0 }
        val im      = DoubleArray(n) { 0.0 }

        fftInPlace(re, im)

        // Only return the first N/2 bins (positive frequencies)
        val halfN = n / 2
        val freqs = FloatArray(halfN) { k -> (k * sampleRateHz / n).toFloat() }
        val mags  = FloatArray(halfN) { k ->
            (sqrt(re[k] * re[k] + im[k] * im[k]) / n * 2).toFloat()
        }

        return FftResult(freqs, mags)
    }

    // ── Cooley-Tukey in-place DIT FFT ────────────────────────────────────────

    private fun fftInPlace(re: DoubleArray, im: DoubleArray) {
        val n = re.size
        // Bit-reversal permutation
        var j = 0
        for (i in 1 until n) {
            var bit = n shr 1
            while (j and bit != 0) {
                j = j xor bit
                bit = bit shr 1
            }
            j = j xor bit
            if (i < j) {
                var tmp = re[i]; re[i] = re[j]; re[j] = tmp
                tmp = im[i];     im[i] = im[j]; im[j] = tmp
            }
        }

        // Butterfly stages
        var len = 2
        while (len <= n) {
            val halfLen = len / 2
            val angStep = -2.0 * PI / len
            val wRe     = cos(angStep)
            val wIm     = sin(angStep)
            var i       = 0
            while (i < n) {
                var curRe = 1.0
                var curIm = 0.0
                for (k in 0 until halfLen) {
                    val uRe = re[i + k]
                    val uIm = im[i + k]
                    val vRe = re[i + k + halfLen] * curRe - im[i + k + halfLen] * curIm
                    val vIm = re[i + k + halfLen] * curIm + im[i + k + halfLen] * curRe
                    re[i + k]         = uRe + vRe
                    im[i + k]         = uIm + vIm
                    re[i + k + halfLen] = uRe - vRe
                    im[i + k + halfLen] = uIm - vIm
                    val nextRe = curRe * wRe - curIm * wIm
                    curIm      = curRe * wIm + curIm * wRe
                    curRe      = nextRe
                }
                i += len
            }
            len = len shl 1
        }
    }

    private fun nextPowerOfTwo(n: Int): Int {
        if (n <= 1) return 1
        val bits = log2(n.toDouble()).toInt()
        return if (1 shl bits == n) n else 1 shl (bits + 1)
    }
}
