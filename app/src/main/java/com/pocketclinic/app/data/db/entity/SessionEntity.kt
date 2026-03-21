package com.pocketclinic.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startedAt: Long,    // epoch ms
    val finishedAt: Long,   // epoch ms
    val notes: String,
)
