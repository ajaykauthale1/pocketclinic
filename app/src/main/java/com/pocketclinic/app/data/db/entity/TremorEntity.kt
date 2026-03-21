package com.pocketclinic.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tremors",
    indices = [Index("sessionId")],
    foreignKeys = [ForeignKey(
        entity    = SessionEntity::class,
        parentColumns = ["id"],
        childColumns  = ["sessionId"],
        onDelete  = ForeignKey.CASCADE,
    )],
)
data class TremorEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val dominantFreqHz: Float,   // dominant tremor frequency
    val amplitude: Float,        // normalised RMS amplitude
    val recordedAt: Long,
)
