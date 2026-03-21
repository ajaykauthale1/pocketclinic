package com.pocketclinic.app.domain.repository

import kotlinx.coroutines.flow.Flow

data class PatientProfile(
    val name: String,
    val dateOfBirth: Long,   // epoch ms
    val gender: String,
    val updatedAt: Long,
)

interface ProfileRepository {
    fun observe(): Flow<PatientProfile?>
    suspend fun save(profile: PatientProfile)
}
