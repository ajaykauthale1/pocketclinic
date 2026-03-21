package com.pocketclinic.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pocketclinic.app.data.db.dao.HeartRateDao
import com.pocketclinic.app.data.db.dao.HearingDao
import com.pocketclinic.app.data.db.dao.ProfileDao
import com.pocketclinic.app.data.db.dao.SessionDao
import com.pocketclinic.app.data.db.dao.TremorDao
import com.pocketclinic.app.data.db.dao.VisualAcuityDao
import com.pocketclinic.app.data.db.entity.HeartRateEntity
import com.pocketclinic.app.data.db.entity.HearingEntity
import com.pocketclinic.app.data.db.entity.ProfileEntity
import com.pocketclinic.app.data.db.entity.SessionEntity
import com.pocketclinic.app.data.db.entity.TremorEntity
import com.pocketclinic.app.data.db.entity.VisualAcuityEntity

@Database(
    entities = [
        SessionEntity::class,
        HeartRateEntity::class,
        TremorEntity::class,
        VisualAcuityEntity::class,
        HearingEntity::class,
        ProfileEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class PocketClinicDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun heartRateDao(): HeartRateDao
    abstract fun tremorDao(): TremorDao
    abstract fun visualAcuityDao(): VisualAcuityDao
    abstract fun hearingDao(): HearingDao
    abstract fun profileDao(): ProfileDao
}
