package com.pocketclinic.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pocketclinic.app.data.db.entity.HeartRateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HeartRateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: HeartRateEntity): Long

    @Query("SELECT * FROM heart_rates WHERE sessionId = :sessionId ORDER BY recordedAt DESC")
    fun getForSession(sessionId: Long): Flow<List<HeartRateEntity>>

    @Query("SELECT * FROM heart_rates WHERE sessionId = :sessionId ORDER BY recordedAt DESC LIMIT 1")
    suspend fun getLatestForSession(sessionId: Long): HeartRateEntity?
}
