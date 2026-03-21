package com.pocketclinic.app.sensor.hearing

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow
import kotlin.math.sin

/**
 * [AudioTrack]-based pure tone generator.
 *
 * Converts dBHL to a linear amplitude using: gain = 10^(dB/20)
 * then writes a sine-wave PCM buffer to an AudioTrack in streaming mode.
 * Full wiring to AudioTrack happens in Phase 7 UI integration.
 */
@Singleton
class AudioTrackPureToneGenerator @Inject constructor() : PureToneGenerator {
    override fun play(freqHz: Int, db: Float) { /* AudioTrack streaming wired in Phase 7 */ }
    override fun stop() { /* AudioTrack.stop() / release() */ }
}
