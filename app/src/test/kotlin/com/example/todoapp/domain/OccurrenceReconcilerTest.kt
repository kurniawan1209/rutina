package com.example.todoapp.domain

import com.example.todoapp.data.local.dao.OccurrenceDao
import com.example.todoapp.data.local.entity.OccurrenceStatus
import com.example.todoapp.data.local.entity.TaskEntity
import com.example.todoapp.data.local.entity.TaskOccurrenceEntity
import com.example.todoapp.data.local.model.TaskWithOccurrence
import com.example.todoapp.domain.model.ScheduleRule
import com.example.todoapp.domain.model.ScheduleRuleParser
import com.example.todoapp.domain.model.ScheduleType
import com.example.todoapp.domain.scheduler.OccurrenceReconciler
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OccurrenceReconcilerTest {

    private class FakeOccurrenceDao : OccurrenceDao {
        val occurrences = mutableListOf<TaskOccurrenceEntity>()
        private var nextId = 1L

        override suspend fun insertOccurrencesIgnore(newItems: List<TaskOccurrenceEntity>) {
            for (item in newItems) {
                val exists = occurrences.any {
                    it.taskId == item.taskId && it.occurrenceDate == item.occurrenceDate
                }
                if (!exists) {
                    occurrences.add(item.copy(id = nextId++))
                }
            }
        }

        override suspend fun getOccurrenceById(id: Long): TaskOccurrenceEntity? {
            return occurrences.find { it.id == id }
        }

        override suspend fun updateStatus(occurrenceId: Long, status: OccurrenceStatus, completedAt: Long?) {
            val index = occurrences.indexOfFirst { it.id == occurrenceId }
            if (index != -1) {
                occurrences[index] = occurrences[index].copy(
                    status = status,
                    completedAt = completedAt?.let { Instant.ofEpochMilli(it) }
                )
            }
        }

        override fun observeOccurrencesForDate(date: String): Flow<List<TaskWithOccurrence>> = flowOf(emptyList())

        override suspend fun getOccurrencesForDateSync(date: String): List<TaskWithOccurrence> = emptyList()

        override fun observeOccurrencesBetween(startDate: String, endDate: String): Flow<List<TaskWithOccurrence>> = flowOf(emptyList())

        override fun observeOverduePending(today: String): Flow<List<TaskWithOccurrence>> = flowOf(emptyList())

        override fun observeOccurrencesForTask(taskId: Long): Flow<List<TaskOccurrenceEntity>> = flowOf(emptyList())

        override suspend fun deletePendingOccurrencesFromDate(taskId: Long, fromDate: String) {
            val date = LocalDate.parse(fromDate)
            occurrences.removeAll {
                it.taskId == taskId && !it.occurrenceDate.isBefore(date) && it.status == OccurrenceStatus.PENDING
            }
        }
    }

    @Test
    fun testReconcileTaskIsIdempotent() = runBlocking {
        val fakeDao = FakeOccurrenceDao()
        val reconciler = OccurrenceReconciler(fakeDao)

        val today = LocalDate.of(2026, 11, 14)
        val dailyRule = ScheduleRule.Daily(1)
        val task = TaskEntity(
            id = 1L,
            title = "Daily Workout",
            scheduleType = ScheduleType.DAILY,
            scheduleRuleJson = ScheduleRuleParser.toJson(dailyRule),
            startDate = today
        )

        // Run reconciliation once
        reconciler.reconcileTaskOccurrences(task, today = today, horizonDays = 10)
        val countAfterFirst = fakeDao.occurrences.size
        assertTrue("Occurrences should be generated", countAfterFirst > 0)

        // Run reconciliation again without schedule edit
        reconciler.reconcileTaskOccurrences(task, today = today, horizonDays = 10)
        val countAfterSecond = fakeDao.occurrences.size

        assertEquals("Reconciliation must not duplicate occurrences", countAfterFirst, countAfterSecond)
    }

    @Test
    fun testScheduleEditPreservesCompletedHistoricalOccurrences() = runBlocking {
        val fakeDao = FakeOccurrenceDao()
        val reconciler = OccurrenceReconciler(fakeDao)

        val yesterday = LocalDate.of(2026, 11, 13)
        val today = LocalDate.of(2026, 11, 14)

        val task = TaskEntity(
            id = 42L,
            title = "Medicine",
            scheduleType = ScheduleType.DAILY,
            scheduleRuleJson = ScheduleRuleParser.toJson(ScheduleRule.Daily(1)),
            startDate = yesterday
        )

        // Seed initial occurrences
        reconciler.reconcileTaskOccurrences(task, today = yesterday, horizonDays = 5)

        // Complete yesterday's occurrence
        val yesterdayOcc = fakeDao.occurrences.first { it.occurrenceDate == yesterday }
        fakeDao.updateStatus(yesterdayOcc.id, OccurrenceStatus.COMPLETED, Instant.now().toEpochMilli())

        // Also complete today's occurrence early
        val todayOcc = fakeDao.occurrences.first { it.occurrenceDate == today }
        fakeDao.updateStatus(todayOcc.id, OccurrenceStatus.COMPLETED, Instant.now().toEpochMilli())

        // Now user edits schedule to Every 3 Days
        val newRule = ScheduleRule.EveryNDays(3, anchorDate = today)
        val updatedTask = task.copy(
            scheduleType = ScheduleType.INTERVAL,
            scheduleRuleJson = ScheduleRuleParser.toJson(newRule)
        )

        // Reconcile with isScheduleEdit = true
        reconciler.reconcileTaskOccurrences(updatedTask, today = today, horizonDays = 10, isScheduleEdit = true)

        // PRD Requirement: Completed historical occurrences must remain unchanged
        val yesterdayStillCompleted = fakeDao.occurrences.find { it.occurrenceDate == yesterday }
        assertEquals(OccurrenceStatus.COMPLETED, yesterdayStillCompleted?.status)

        // Today's completed occurrence was already completed, so deletePendingOccurrencesFromDate leaves it
        val todayStillCompleted = fakeDao.occurrences.find { it.occurrenceDate == today }
        assertEquals(OccurrenceStatus.COMPLETED, todayStillCompleted?.status)
    }

    @Test
    fun testIndependentOccurrenceCompletion() = runBlocking {
        val fakeDao = FakeOccurrenceDao()
        val day1 = LocalDate.of(2026, 11, 14)
        val day2 = LocalDate.of(2026, 11, 15)

        fakeDao.insertOccurrencesIgnore(
            listOf(
                TaskOccurrenceEntity(taskId = 10L, occurrenceDate = day1, status = OccurrenceStatus.PENDING),
                TaskOccurrenceEntity(taskId = 10L, occurrenceDate = day2, status = OccurrenceStatus.PENDING)
            )
        )

        // Complete day1
        val occ1 = fakeDao.occurrences.first { it.occurrenceDate == day1 }
        fakeDao.updateStatus(occ1.id, OccurrenceStatus.COMPLETED, Instant.now().toEpochMilli())

        // Verify day1 is completed, but day2 remains pending
        val checkDay1 = fakeDao.occurrences.first { it.occurrenceDate == day1 }
        val checkDay2 = fakeDao.occurrences.first { it.occurrenceDate == day2 }

        assertEquals(OccurrenceStatus.COMPLETED, checkDay1.status)
        assertEquals(OccurrenceStatus.PENDING, checkDay2.status)
    }
}
