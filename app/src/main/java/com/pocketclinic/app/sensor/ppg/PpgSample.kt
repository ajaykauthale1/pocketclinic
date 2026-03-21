package com.pocketclinic.app.sensor.ppg

/**
 * A processed PPG sample emitted by [PpgProcessor].
 *
 * @param bpm      Instantaneous heart rate (BPM); null if not yet stable.
 * @param rmssd    HRV RMSSD in ms; null if fewer than 3 R-peaks detected.
 * @param waveform Normalised signal window for waveform display (values 0..1).
 */
data class PpgSample(
    val bpm: Float?,
    val rmssd: Float?,
    val waveform: FloatArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PpgSample) return false
        return bpm == other.bpm && rmssd == other.rmssd && waveform.contentEquals(other.waveform)
    }
    override fun hashCode(): Int = 31 * (31 * (bpm?.hashCode() ?: 0) + (rmssd?.hashCode() ?: 0)) + waveform.contentHashCode()
}
