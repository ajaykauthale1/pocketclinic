package com.pocketclinic.app.domain.repository

import com.pocketclinic.app.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun observeAll(): Flow<List<Session>>
    suspend fun getById(id: Long): Session?
    suspend fun save(session: Session): Long
    suspend fun delete(session: Session)
}
