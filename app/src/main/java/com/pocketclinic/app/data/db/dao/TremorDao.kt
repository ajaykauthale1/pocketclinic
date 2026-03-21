package com.pocketclinic.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pocketclinic.app.data.db.entity.TremorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TremorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TremorEntity): Long

    @Query("SELECT * FROM tremors WHERE sessionId = :sessionId ORDER BY recordedAt DESC")
    fun getForSession(sessionId: Long): Flow<List<TremorEntity>>

    @Query("SELECT * FROM tremors WHERE sessionId = :sessionId ORDER BY recordedAt DESC LIMIT 1")
    suspend fun getLatestForSession(sessionId: Long): TremorEntity?

    @Query("DELETE FROM tremors WHERE sessionId = :sessionId")
    suspend fun deleteForSession(sessionId: Long)
}
