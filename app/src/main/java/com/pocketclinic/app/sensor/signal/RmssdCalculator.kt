package com.pocketclinic.app.sensor.signal

import kotlin.math.sqrt

/**
 * Computes RMSSD (Root Mean Square of Successive Differences) — the standard
 * short-term HRV metric reflecting parasympathetic nervous system activity.
 *
 * Clinical interpretation:
 *   - RMSSD > 50 ms  →  high HRV, relaxed / recovered
 *   - RMSSD 20–50 ms →  moderate HRV, normal resting
 *   - RMSSD < 20 ms  →  low HRV, stress / fatigue flag
 */
class RmssdCalculator {

    /**
     * Compute RMSSD from a list of R-peak timestamps in milliseconds.
     *
     * @param peakTimestampsMs Sorted list of timestamps (ms) for each detected heartbeat.
     * @return RMSSD in milliseconds, or null if fewer than 3 timestamps are provided
     *         (need ≥ 2 RR intervals = ≥ 3 timestamps to compute at least one successive difference).
     */
    fun compute(peakTimestampsMs: List<Long>): Float? {
        if (peakTimestampsMs.size < 3) return null  // need ≥ 2 RR intervals

        val rrIntervals = peakTimestampsMs
            .zipWithNext { a, b -> (b - a).toDouble() }  // RR intervals in ms

        val successiveDiffs = rrIntervals
            .zipWithNext { a, b -> b - a }               // first differences of RR

        if (successiveDiffs.isEmpty()) return null

        val meanSquaredDiff = successiveDiffs
            .map { it * it }
            .average()

        return sqrt(meanSquaredDiff).toFloat()
    }
}
