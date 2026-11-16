package com.example.todoapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

enum class OccurrenceStatus {
    PENDING,
    COMPLETED
}

@Entity(
    tableName = "task_occurrences",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["taskId", "occurrenceDate"], unique = true),
        Index(value = ["occurrenceDate", "status"])
    ]
)
data class TaskOccurrenceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long,
    val occurrenceDate: LocalDate,
    val status: OccurrenceStatus = OccurrenceStatus.PENDING,
    val completedAt: Instant? = null,
    val createdAt: Instant = Instant.now()
)
