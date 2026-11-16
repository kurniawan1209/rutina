package com.example.todoapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.todoapp.data.local.entity.OccurrenceStatus
import com.example.todoapp.data.local.entity.TaskOccurrenceEntity
import com.example.todoapp.data.local.model.TaskWithOccurrence
import kotlinx.coroutines.flow.Flow

@Dao
interface OccurrenceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOccurrencesIgnore(occurrences: List<TaskOccurrenceEntity>)

    @Query("SELECT * FROM task_occurrences WHERE id = :id")
    suspend fun getOccurrenceById(id: Long): TaskOccurrenceEntity?

    @Query("UPDATE task_occurrences SET status = :status, completedAt = :completedAt WHERE id = :occurrenceId")
    suspend fun updateStatus(occurrenceId: Long, status: OccurrenceStatus, completedAt: Long?)

    @Transaction
    @Query("""
        SELECT task_occurrences.* FROM task_occurrences 
        INNER JOIN tasks ON task_occurrences.taskId = tasks.id
        WHERE task_occurrences.occurrenceDate = :date AND tasks.isArchived = 0
        ORDER BY task_occurrences.status ASC, task_occurrences.id ASC
    """)
    fun observeOccurrencesForDate(date: String): Flow<List<TaskWithOccurrence>>

    @Transaction
    @Query("""
        SELECT task_occurrences.* FROM task_occurrences 
        INNER JOIN tasks ON task_occurrences.taskId = tasks.id
        WHERE task_occurrences.occurrenceDate = :date AND tasks.isArchived = 0
        ORDER BY task_occurrences.status ASC, task_occurrences.id ASC
    """)
    suspend fun getOccurrencesForDateSync(date: String): List<TaskWithOccurrence>

    @Transaction
    @Query("""
        SELECT task_occurrences.* FROM task_occurrences 
        INNER JOIN tasks ON task_occurrences.taskId = tasks.id
        WHERE task_occurrences.occurrenceDate >= :startDate AND task_occurrences.occurrenceDate <= :endDate AND tasks.isArchived = 0
        ORDER BY task_occurrences.occurrenceDate ASC, task_occurrences.status ASC
    """)
    fun observeOccurrencesBetween(startDate: String, endDate: String): Flow<List<TaskWithOccurrence>>

    @Transaction
    @Query("""
        SELECT task_occurrences.* FROM task_occurrences 
        INNER JOIN tasks ON task_occurrences.taskId = tasks.id
        WHERE task_occurrences.occurrenceDate < :today AND task_occurrences.status = 'PENDING' AND tasks.isArchived = 0
        ORDER BY task_occurrences.occurrenceDate ASC
    """)
    fun observeOverduePending(today: String): Flow<List<TaskWithOccurrence>>

    @Query("SELECT * FROM task_occurrences WHERE taskId = :taskId ORDER BY occurrenceDate DESC")
    fun observeOccurrencesForTask(taskId: Long): Flow<List<TaskOccurrenceEntity>>

    @Query("""
        DELETE FROM task_occurrences 
        WHERE taskId = :taskId AND occurrenceDate >= :fromDate AND status = 'PENDING'
    """)
    suspend fun deletePendingOccurrencesFromDate(taskId: Long, fromDate: String)
}
