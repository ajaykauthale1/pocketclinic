package com.pocketclinic.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val id: Int = 1,   // singleton row
    val name: String,
    val dateOfBirth: Long,         // epoch ms
    val gender: String,
    val updatedAt: Long,
)
