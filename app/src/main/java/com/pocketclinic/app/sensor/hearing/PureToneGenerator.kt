package com.pocketclinic.app.sensor.hearing

/**
 * Generates pure-tone sine waves at a given frequency and amplitude using Android's [AudioTrack].
 *
 * The amplitude is specified in dB SPL; internally this maps to a normalised linear gain.
 * Full implementation wired in Phase 7 UI integration.
 */
interface PureToneGenerator {
    /** Play a continuous tone at [freqHz] with level [db] (10–90 dBHL). */
    fun play(freqHz: Int, db: Float)
    /** Stop any currently playing tone. */
    fun stop()
}
