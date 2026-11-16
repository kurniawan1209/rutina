package com.example.todoapp.domain.scheduler

import com.example.todoapp.data.local.dao.OccurrenceDao
import com.example.todoapp.data.local.entity.OccurrenceStatus
import com.example.todoapp.data.local.entity.TaskEntity
import com.example.todoapp.data.local.entity.TaskOccurrenceEntity
import com.example.todoapp.domain.model.ScheduleRuleParser
import java.time.Instant
import java.time.LocalDate

class OccurrenceReconciler(
    private val occurrenceDao: OccurrenceDao
) {

    /**
     * Reconciles occurrences for a task across a rolling horizon (default: today + [horizonDays]).
     *
     * @param isScheduleEdit If true, removes pending future occurrences from [today] onwards
     *                       before regenerating, preserving any completed occurrences.
     */
    suspend fun reconcileTaskOccurrences(
        task: TaskEntity,
        today: LocalDate = LocalDate.now(),
        horizonDays: Long = 90,
        isScheduleEdit: Boolean = false
    ) {
        val windowStart = if (task.startDate.isAfter(today)) task.startDate else today.minusDays(7)
        val windowEnd = today.plusDays(horizonDays)

        if (isScheduleEdit) {
            // PRD 5.4: If the user changes a recurring schedule, already completed historical occurrences
            // remain unchanged. Rebuild only future, uncompleted occurrences.
            occurrenceDao.deletePendingOccurrencesFromDate(task.id, today.toString())
        }

        val rule = ScheduleRuleParser.fromJson(task.scheduleType, task.scheduleRuleJson)
        val dates = RecurrenceCalculator.calculateOccurrences(
            rule = rule,
            startDate = task.startDate,
            endDate = task.endDate,
            windowStart = windowStart,
            windowEnd = windowEnd
        )

        val entities = dates.map { date ->
            TaskOccurrenceEntity(
                taskId = task.id,
                occurrenceDate = date,
                status = OccurrenceStatus.PENDING,
                completedAt = null,
                createdAt = Instant.now()
            )
        }

        if (entities.isNotEmpty()) {
            occurrenceDao.insertOccurrencesIgnore(entities)
        }
    }
}
