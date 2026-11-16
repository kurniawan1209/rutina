package com.example.todoapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todoapp.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): TaskEntity?

    @Query("SELECT * FROM tasks WHERE isArchived = 0 ORDER BY createdAt DESC")
    fun observeActiveTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isArchived = 1 ORDER BY updatedAt DESC")
    fun observeArchivedTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isArchived = 0")
    suspend fun getActiveTasksSync(): List<TaskEntity>

    @Query("UPDATE tasks SET isArchived = :isArchived, updatedAt = :updatedAt WHERE id = :taskId")
    suspend fun setArchived(taskId: Long, isArchived: Boolean, updatedAt: Long)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: Long)
}
