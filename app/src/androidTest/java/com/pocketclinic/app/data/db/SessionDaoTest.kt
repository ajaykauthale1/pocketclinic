package com.pocketclinic.app.data.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.pocketclinic.app.data.db.dao.SessionDao
import com.pocketclinic.app.data.db.entity.SessionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class SessionDaoTest {

    private lateinit var db: PocketClinicDatabase
    private lateinit var dao: SessionDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PocketClinicDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = db.sessionDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertSession_and_getAllSessions_returns_it() = runTest {
        val session = SessionEntity(id = 1, startedAt = 1_000L, finishedAt = 2_000L, notes = "")
        dao.insert(session)
        val all = dao.getAllSessions().first()
        assertThat(all).hasSize(1)
        assertThat(all.first().id).isEqualTo(1)
    }

    @Test
    fun getSessionById_returns_correct_entity() = runTest {
        val session = SessionEntity(id = 42, startedAt = 100L, finishedAt = 200L, notes = "test")
        dao.insert(session)
        val found = dao.getSessionById(42)
        assertThat(found).isNotNull()
        assertThat(found!!.notes).isEqualTo("test")
    }

    @Test
    fun getSessionById_returns_null_for_missing_id() = runTest {
        val found = dao.getSessionById(999)
        assertThat(found).isNull()
    }

    @Test
    fun deleteSession_removes_it_from_flow() = runTest {
        val session = SessionEntity(id = 7, startedAt = 0L, finishedAt = 1L, notes = "")
        dao.insert(session)
        dao.delete(session)
        val all = dao.getAllSessions().first()
        assertThat(all).isEmpty()
    }

    @Test
    fun getAllSessions_emits_empty_list_when_table_is_empty() = runTest {
        val all = dao.getAllSessions().first()
        assertThat(all).isEmpty()
    }

    @Test
    fun insert_multiple_sessions_all_appear_in_flow() = runTest {
        dao.insert(SessionEntity(id = 1, startedAt = 100L, finishedAt = 200L, notes = ""))
        dao.insert(SessionEntity(id = 2, startedAt = 300L, finishedAt = 400L, notes = ""))
        dao.insert(SessionEntity(id = 3, startedAt = 500L, finishedAt = 600L, notes = ""))
        val all = dao.getAllSessions().first()
        assertThat(all).hasSize(3)
    }

    @Test
    fun sessions_are_ordered_by_startedAt_descending() = runTest {
        dao.insert(SessionEntity(id = 1, startedAt = 100L, finishedAt = 200L, notes = "first"))
        dao.insert(SessionEntity(id = 2, startedAt = 900L, finishedAt = 1000L, notes = "third"))
        dao.insert(SessionEntity(id = 3, startedAt = 500L, finishedAt = 600L, notes = "second"))
        val all = dao.getAllSessions().first()
        assertThat(all[0].startedAt).isGreaterThan(all[1].startedAt)
        assertThat(all[1].startedAt).isGreaterThan(all[2].startedAt)
    }
}
