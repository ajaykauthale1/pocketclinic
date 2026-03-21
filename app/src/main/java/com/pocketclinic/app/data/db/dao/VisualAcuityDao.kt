package com.pocketclinic.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pocketclinic.app.data.db.entity.VisualAcuityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VisualAcuityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: VisualAcuityEntity): Long

    @Query("SELECT * FROM visual_acuity WHERE sessionId = :sessionId ORDER BY recordedAt DESC")
    fun getForSession(sessionId: Long): Flow<List<VisualAcuityEntity>>

    @Query("SELECT * FROM visual_acuity WHERE sessionId = :sessionId ORDER BY recordedAt DESC LIMIT 1")
    suspend fun getLatestForSession(sessionId: Long): VisualAcuityEntity?

    @Query("DELETE FROM visual_acuity WHERE sessionId = :sessionId")
    suspend fun deleteForSession(sessionId: Long)
}
