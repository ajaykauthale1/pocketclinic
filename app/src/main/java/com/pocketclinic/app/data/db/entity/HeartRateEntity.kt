package com.pocketclinic.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "heart_rates",
    indices = [Index("sessionId")],
    foreignKeys = [ForeignKey(
        entity    = SessionEntity::class,
        parentColumns = ["id"],
        childColumns  = ["sessionId"],
        onDelete  = ForeignKey.CASCADE,
    )],
)
data class HeartRateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val bpm: Float,
    val rmssd: Float?,       // HRV in ms; null if insufficient data
    val recordedAt: Long,    // epoch ms
)
