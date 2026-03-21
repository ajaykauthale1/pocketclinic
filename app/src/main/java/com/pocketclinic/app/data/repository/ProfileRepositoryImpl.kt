package com.pocketclinic.app.data.repository

import com.pocketclinic.app.data.db.dao.ProfileDao
import com.pocketclinic.app.data.db.entity.ProfileEntity
import com.pocketclinic.app.domain.repository.PatientProfile
import com.pocketclinic.app.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val dao: ProfileDao,
) : ProfileRepository {

    override fun observe(): Flow<PatientProfile?> =
        dao.observe().map { it?.toDomain() }

    override suspend fun save(profile: PatientProfile) =
        dao.upsert(profile.toEntity())
}

private fun ProfileEntity.toDomain() = PatientProfile(
    name        = name,
    dateOfBirth = dateOfBirth,
    gender      = gender,
    updatedAt   = updatedAt,
)

private fun PatientProfile.toEntity() = ProfileEntity(
    id          = 1,
    name        = name,
    dateOfBirth = dateOfBirth,
    gender      = gender,
    updatedAt   = updatedAt,
)
