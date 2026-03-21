package com.pocketclinic.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pocketclinic.app.data.db.entity.HearingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HearingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: HearingEntity): Long

    @Query("SELECT * FROM hearing WHERE sessionId = :sessionId ORDER BY recordedAt DESC")
    fun getForSession(sessionId: Long): Flow<List<HearingEntity>>

    @Query("SELECT * FROM hearing WHERE sessionId = :sessionId ORDER BY frequencyHz ASC")
    suspend fun getAudiogramForSession(sessionId: Long): List<HearingEntity>

    @Query("DELETE FROM hearing WHERE sessionId = :sessionId")
    suspend fun deleteForSession(sessionId: Long)
}
