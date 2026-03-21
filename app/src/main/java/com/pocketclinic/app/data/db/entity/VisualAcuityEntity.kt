package com.pocketclinic.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "visual_acuity",
    indices = [Index("sessionId")],
    foreignKeys = [ForeignKey(
        entity    = SessionEntity::class,
        parentColumns = ["id"],
        childColumns  = ["sessionId"],
        onDelete  = ForeignKey.CASCADE,
    )],
)
data class VisualAcuityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val logmar: Float,       // LogMAR score (0 = 20/20, positive = worse)
    val distanceCm: Float,   // measured face-to-screen distance
    val recordedAt: Long,
)
