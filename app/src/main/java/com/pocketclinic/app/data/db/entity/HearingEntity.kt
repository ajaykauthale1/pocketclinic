package com.pocketclinic.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "hearing",
    indices = [Index("sessionId")],
    foreignKeys = [ForeignKey(
        entity    = SessionEntity::class,
        parentColumns = ["id"],
        childColumns  = ["sessionId"],
        onDelete  = ForeignKey.CASCADE,
    )],
)
data class HearingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val frequencyHz: Int,        // pure-tone frequency tested
    val thresholdDb: Float,      // minimum audible level
    val recordedAt: Long,
)
