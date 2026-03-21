package com.pocketclinic.app.data.repository

import com.pocketclinic.app.data.db.dao.SessionDao
import com.pocketclinic.app.data.db.entity.SessionEntity
import com.pocketclinic.app.domain.model.Session
import com.pocketclinic.app.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val dao: SessionDao,
) : SessionRepository {

    override fun observeAll(): Flow<List<Session>> =
        dao.getAllSessions().map { list -> list.map(SessionEntity::toDomain) }

    override suspend fun getById(id: Long): Session? =
        dao.getSessionById(id)?.toDomain()

    override suspend fun save(session: Session): Long =
        dao.insert(session.toEntity())

    override suspend fun delete(session: Session) =
        dao.delete(session.toEntity())
}

private fun SessionEntity.toDomain() = Session(
    id         = id,
    startedAt  = startedAt,
    finishedAt = finishedAt,
    notes      = notes,
)

private fun Session.toEntity() = SessionEntity(
    id         = id,
    startedAt  = startedAt,
    finishedAt = finishedAt,
    notes      = notes,
)
