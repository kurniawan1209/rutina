package com.example.todoapp.data.repository

import android.content.Context
import com.example.todoapp.data.local.AppDatabase
import com.example.todoapp.data.local.dao.OccurrenceDao
import com.example.todoapp.data.local.dao.TaskDao
import com.example.todoapp.data.local.entity.OccurrenceStatus
import com.example.todoapp.data.local.entity.TaskEntity
import com.example.todoapp.data.local.entity.TaskOccurrenceEntity
import com.example.todoapp.data.local.model.TaskWithOccurrence
import com.example.todoapp.domain.model.ScheduleRule
import com.example.todoapp.domain.model.ScheduleRuleParser
import com.example.todoapp.domain.model.ScheduleType
import com.example.todoapp.domain.scheduler.OccurrenceReconciler
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

data class TrainingScheduleItem(
    val title: String,
    val notes: String,
    val dayOfWeek: DayOfWeek,
    val colorHex: String,
    val iconKey: String
)

val DEFAULT_TRAINING_SCHEDULE = listOf(
    TrainingScheduleItem("Easy run 4–5 km", "Intensitas: Ringan", DayOfWeek.MONDAY, "#3525CD", "fitness_center"),
    TrainingScheduleItem("Gym Lower Body", "Intensitas: Sedang", DayOfWeek.TUESDAY, "#006A7C", "fitness_center"),
    TrainingScheduleItem("Swimming 20–30 menit", "Intensitas: Ringan", DayOfWeek.WEDNESDAY, "#00505F", "ecg_heart"),
    TrainingScheduleItem("Gym Upper Body", "Intensitas: Sedang", DayOfWeek.THURSDAY, "#006A7C", "fitness_center"),
    TrainingScheduleItem("Tempo run 5–7 km total", "Intensitas: Tinggi", DayOfWeek.FRIDAY, "#BA1A1A", "fitness_center"),
    TrainingScheduleItem("Long run atau easy/recovery", "Intensitas: Sesuai jadwal", DayOfWeek.SATURDAY, "#FEA619", "fitness_center"),
    TrainingScheduleItem("Rest / recovery", "Intensitas: Sangat ringan", DayOfWeek.SUNDAY, "#A5B4FC", "cottage")
)

class TaskRepository(
    private val taskDao: TaskDao,
    private val occurrenceDao: OccurrenceDao,
    private val reconciler: OccurrenceReconciler = OccurrenceReconciler(occurrenceDao)
) {

    fun observeTodayOccurrences(today: LocalDate = LocalDate.now()): Flow<List<TaskWithOccurrence>> {
        return occurrenceDao.observeOccurrencesForDate(today.toString())
    }

    suspend fun getTodayOccurrencesSync(today: LocalDate = LocalDate.now()): List<TaskWithOccurrence> {
        return occurrenceDao.getOccurrencesForDateSync(today.toString())
    }

    fun observeUpcomingOccurrences(
        startDate: LocalDate = LocalDate.now().plusDays(1),
        endDate: LocalDate = LocalDate.now().plusDays(30)
    ): Flow<List<TaskWithOccurrence>> {
        return occurrenceDao.observeOccurrencesBetween(startDate.toString(), endDate.toString())
    }

    fun observeOverdueOccurrences(today: LocalDate = LocalDate.now()): Flow<List<TaskWithOccurrence>> {
        return occurrenceDao.observeOverduePending(today.toString())
    }

    fun observeActiveTasks(): Flow<List<TaskEntity>> {
        return taskDao.observeActiveTasks()
    }

    fun observeArchivedTasks(): Flow<List<TaskEntity>> {
        return taskDao.observeArchivedTasks()
    }

    suspend fun getTaskById(taskId: Long): TaskEntity? {
        return taskDao.getTaskById(taskId)
    }

    fun observeOccurrencesForTask(taskId: Long): Flow<List<TaskOccurrenceEntity>> {
        return occurrenceDao.observeOccurrencesForTask(taskId)
    }

    suspend fun saveTask(
        task: TaskEntity,
        rule: ScheduleRule,
        today: LocalDate = LocalDate.now()
    ): Long {
        val ruleJson = ScheduleRuleParser.toJson(rule)
        val entityToSave = task.copy(
            scheduleType = rule.type,
            scheduleRuleJson = ruleJson,
            updatedAt = Instant.now()
        )

        val isEdit = task.id > 0
        val savedId = if (isEdit) {
            taskDao.updateTask(entityToSave)
            task.id
        } else {
            taskDao.insertTask(entityToSave)
        }

        val refreshedTask = entityToSave.copy(id = savedId)
        reconciler.reconcileTaskOccurrences(
            task = refreshedTask,
            today = today,
            isScheduleEdit = isEdit
        )
        return savedId
    }

    suspend fun toggleOccurrenceStatus(occurrenceId: Long, isCompleted: Boolean) {
        val newStatus = if (isCompleted) OccurrenceStatus.COMPLETED else OccurrenceStatus.PENDING
        val completedAt = if (isCompleted) Instant.now().toEpochMilli() else null
        occurrenceDao.updateStatus(occurrenceId, newStatus, completedAt)
    }

    suspend fun setTaskArchived(taskId: Long, isArchived: Boolean) {
        taskDao.setArchived(taskId, isArchived, Instant.now().toEpochMilli())
    }

    suspend fun deleteTask(taskId: Long) {
        taskDao.deleteTask(taskId)
    }

    suspend fun reconcileAllActiveTasks(today: LocalDate = LocalDate.now()) {
        val tasks = taskDao.getActiveTasksSync()
        for (task in tasks) {
            reconciler.reconcileTaskOccurrences(task, today = today, isScheduleEdit = false)
        }
    }

    suspend fun seedDefaultTrainingScheduleIfNotExists(today: LocalDate = LocalDate.now()) {
        val tasks = taskDao.getActiveTasksSync()
        if (tasks.isEmpty()) {
            loadTrainingSchedule(today)
        }
    }

    suspend fun loadTrainingSchedule(today: LocalDate = LocalDate.now()): Int {
        val existingTitles = taskDao.getActiveTasksSync().map { it.title.trim().lowercase() }.toSet()
        val mondayThisWeek = today.minusDays((today.dayOfWeek.value - 1).toLong())
        var addedCount = 0

        for (item in DEFAULT_TRAINING_SCHEDULE) {
            if (item.title.trim().lowercase() in existingTitles) continue

            val rule = ScheduleRule.Weekly(setOf(item.dayOfWeek))
            val entity = TaskEntity(
                title = item.title,
                notes = item.notes,
                scheduleType = ScheduleType.WEEKLY,
                scheduleRuleJson = ScheduleRuleParser.toJson(rule),
                startDate = mondayThisWeek,
                endDate = null,
                colorHex = "${item.colorHex}:${item.iconKey}",
                isArchived = false
            )
            saveTask(entity, rule, today)
            addedCount++
        }
        return addedCount
    }

    companion object {
        @Volatile
        private var INSTANCE: TaskRepository? = null

        fun getInstance(context: Context): TaskRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: run {
                    val db = AppDatabase.getInstance(context)
                    TaskRepository(db.taskDao(), db.occurrenceDao()).also { INSTANCE = it }
                }
            }
        }
    }
}
