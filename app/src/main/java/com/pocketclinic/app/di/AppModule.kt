package com.pocketclinic.app.di

import android.content.Context
import androidx.room.Room
import com.pocketclinic.app.data.db.PocketClinicDatabase
import com.pocketclinic.app.data.db.dao.HearingDao
import com.pocketclinic.app.data.db.dao.ProfileDao
import com.pocketclinic.app.data.db.dao.SessionDao
import com.pocketclinic.app.data.db.dao.TremorDao
import com.pocketclinic.app.data.db.dao.VisualAcuityDao
import com.pocketclinic.app.data.repository.ProfileRepositoryImpl
import com.pocketclinic.app.data.repository.SessionRepositoryImpl
import com.pocketclinic.app.domain.repository.ProfileRepository
import com.pocketclinic.app.domain.repository.SessionRepository
import com.pocketclinic.app.sensor.ppg.CameraXPpgProcessor
import com.pocketclinic.app.sensor.ppg.PpgProcessor
import com.pocketclinic.app.sensor.hearing.AudioTrackPureToneGenerator
import com.pocketclinic.app.sensor.hearing.PureToneGenerator
import com.pocketclinic.app.sensor.tremor.GyroscopeTremorProcessor
import com.pocketclinic.app.sensor.tremor.TremorProcessor
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository

    @Binds
    @Singleton
    abstract fun bindPpgProcessor(impl: CameraXPpgProcessor): PpgProcessor

    @Binds
    @Singleton
    abstract fun bindTremorProcessor(impl: GyroscopeTremorProcessor): TremorProcessor

    @Binds
    @Singleton
    abstract fun bindPureToneGenerator(impl: AudioTrackPureToneGenerator): PureToneGenerator

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    companion object {

        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext ctx: Context): PocketClinicDatabase =
            Room.databaseBuilder(ctx, PocketClinicDatabase::class.java, "pocketclinic.db")
                .build()

        @Provides
        fun provideSessionDao(db: PocketClinicDatabase): SessionDao = db.sessionDao()

        @Provides
        fun provideTremorDao(db: PocketClinicDatabase): TremorDao = db.tremorDao()

        @Provides
        fun provideVisualAcuityDao(db: PocketClinicDatabase): VisualAcuityDao = db.visualAcuityDao()

        @Provides
        fun provideHearingDao(db: PocketClinicDatabase): HearingDao = db.hearingDao()

        @Provides
        fun provideProfileDao(db: PocketClinicDatabase): ProfileDao = db.profileDao()
    }
}
