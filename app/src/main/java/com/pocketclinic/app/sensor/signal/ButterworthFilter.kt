package com.pocketclinic.app.sensor.signal

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

/**
 * Configuration for a Butterworth bandpass filter.
 *
 * @param lowCutHz     Lower cutoff frequency in Hz.
 * @param highCutHz    Upper cutoff frequency in Hz.
 * @param sampleRateHz Sampling frequency in Hz.
 * @param order        Filter order (must be even; bandpass has `order` poles total, derived from
 *                     an `order/2`-pole LP prototype). Default 4 → 4-pole bandpass, 2 biquads.
 */
data class FilterConfig(
    val lowCutHz: Double,
    val highCutHz: Double,
    val sampleRateHz: Double,
    val order: Int = 4,
)

/**
 * Direct-Form II Transposed Butterworth bandpass IIR filter.
 *
 * Implemented as a cascade of second-order sections (biquads) derived analytically:
 *   1. Pre-warp the cutoff frequencies via the bilinear transform.
 *   2. Compute LP prototype poles for a Butterworth of order `lpOrder = order/2`.
 *   3. Apply LP-to-BP frequency transformation to obtain s-domain bandpass poles.
 *   4. Map s-domain poles to z-domain via the bilinear transform.
 *   5. Build biquad sections (b0, 0, -b0, a1, a2) from each conjugate pole pair.
 *   6. Normalize each section's passband gain to 1 at the geometric centre frequency.
 *
 * Usage: each [ButterworthFilter] instance maintains its own delay-line state,
 * so it is NOT thread-safe. Create separate instances per channel.
 */
class ButterworthFilter(config: FilterConfig) {

    private val sections: List<BiquadSection>
    private val states: Array<DoubleArray>

    init {
        sections = computeCoefficients(config)
        states   = Array(sections.size) { DoubleArray(2) }
    }

    /** Apply the filter to [input] and return filtered output of the same length. */
    fun filter(input: DoubleArray): DoubleArray {
        if (input.isEmpty()) return DoubleArray(0)
        val output = DoubleArray(input.size)
        for (i in input.indices) {
            var x = input[i]
            for (s in sections.indices) {
                val sec = sections[s]
                val w0  = x - sec.a1 * states[s][0] - sec.a2 * states[s][1]
                val y   = sec.b0 * w0 + sec.b1 * states[s][0] + sec.b2 * states[s][1]
                states[s][1] = states[s][0]
                states[s][0] = w0
                x = y
            }
            output[i] = x
        }
        return output
    }

    // ── Internal types ───────────────────────────────────────────────────────

    private data class BiquadSection(
        val b0: Double, val b1: Double, val b2: Double,
        val a1: Double, val a2: Double,
    )

    // ── Coefficient computation ──────────────────────────────────────────────

    private fun computeCoefficients(cfg: FilterConfig): List<BiquadSection> {
        val fs      = cfg.sampleRateHz
        val twoFs   = 2.0 * fs
        val lpOrder = cfg.order / 2          // LP prototype order

        // Pre-warp cutoff frequencies (bilinear transform)
        val omegaL   = twoFs * tan(PI * cfg.lowCutHz  / fs)
        val omegaH   = twoFs * tan(PI * cfg.highCutHz / fs)
        val bw       = omegaH - omegaL
        val omega0Sq = omegaL * omegaH       // geometric centre squared

        val result = mutableListOf<BiquadSection>()

        // Loop over LP conjugate pairs (each odd-indexed pole and its conjugate).
        // For lpOrder=2: one pair (k=1), producing 2 BP biquads.
        // For lpOrder=4: two pairs (k=1,3), producing 4 BP biquads.
        for (j in 1..lpOrder / 2) {
            val k     = 2 * j - 1   // 1, 3, 5, …
            val theta = PI * (2.0 * k - 1.0) / (2.0 * lpOrder)

            // k-th Butterworth LP prototype pole (left half-plane)
            val lpRe = -sin(theta)
            val lpIm =  cos(theta)

            // LP-to-BP transformation: s_LP → (s² + ω₀²) / (bw · s)
            // Each LP pole s_LP yields two BP poles via s² − bw·s_LP·s + ω₀² = 0
            val aRe = bw * lpRe
            val aIm = bw * lpIm

            // discriminant = (bw·s_LP)² − 4·ω₀²
            val discRe = aRe * aRe - aIm * aIm - 4.0 * omega0Sq
            val discIm = 2.0 * aRe * aIm

            // Complex square root (polar form, always principal branch)
            val discMag   = hypot(discRe, discIm)
            val discAngle = atan2(discIm, discRe)
            val sqrtMag   = sqrt(discMag)
            val sqrtRe    = sqrtMag * cos(discAngle / 2.0)
            val sqrtIm    = sqrtMag * sin(discAngle / 2.0)

            // Two s-domain BP poles
            val p1Re = (aRe + sqrtRe) / 2.0
            val p1Im = (aIm + sqrtIm) / 2.0
            val p2Re = (aRe - sqrtRe) / 2.0
            val p2Im = (aIm - sqrtIm) / 2.0

            // Form one biquad per conjugate pole pair (p, p̄)
            result.add(polePairToBiquad(p1Re, p1Im, twoFs, omega0Sq))
            result.add(polePairToBiquad(p2Re, p2Im, twoFs, omega0Sq))
        }

        return result
    }

    /**
     * Map one s-domain pole [sRe]+j[sIm] (and its conjugate) to a biquad section.
     *
     * Bilinear transform: z = (2fs + s) / (2fs − s)
     *
     * Bandpass numerator has zeros at z=+1 (DC) and z=−1 (Nyquist):
     *   b(z) = K·(z² − 1)   →   b0=K, b1=0, b2=−K
     *
     * K is chosen so that the section gain equals 1 at the geometric centre frequency.
     */
    private fun polePairToBiquad(sRe: Double, sIm: Double, twoFs: Double, omega0Sq: Double): BiquadSection {
        // Bilinear transform of the pole
        val numRe    = twoFs + sRe
        val numIm    = sIm
        val denRe    = twoFs - sRe
        val denIm    = -sIm
        val denMagSq = denRe * denRe + denIm * denIm

        val z1Re = (numRe * denRe + numIm * denIm) / denMagSq
        val z1Im = (numIm * denRe - numRe * denIm) / denMagSq

        // Denominator polynomial: (z − z1)(z − z̄1) = z² − 2·Re(z1)·z + |z1|²
        // In z⁻¹ form: 1 − 2·Re(z1)·z⁻¹ + |z1|²·z⁻²
        val a1 = -2.0 * z1Re
        val a2 = z1Re * z1Re + z1Im * z1Im

        // Gain normalisation at the digital centre frequency ωd = 2·atan(√ω₀²/2fs)
        val omega0  = sqrt(omega0Sq)
        val omegaD  = 2.0 * atan(omega0 / twoFs)
        val cosW    = cos(omegaD)
        val sinW    = sin(omegaD)
        val cos2W   = cos(2.0 * omegaD)
        val sin2W   = sin(2.0 * omegaD)

        // Numerator magnitude at ωd: |1 − e^{−2jωd}| = 2·|sin(ωd)|
        val numMagAtCenter = 2.0 * abs(sinW)

        // Denominator magnitude at ωd
        val denAtCenterRe = 1.0 + a1 * cosW + a2 * cos2W
        val denAtCenterIm = -(a1 * sinW + a2 * sin2W)
        val denMagAtCenter = hypot(denAtCenterRe, denAtCenterIm)

        val K = if (numMagAtCenter > 1e-10) denMagAtCenter / numMagAtCenter else 1.0

        return BiquadSection(b0 = K, b1 = 0.0, b2 = -K, a1 = a1, a2 = a2)
    }
}
