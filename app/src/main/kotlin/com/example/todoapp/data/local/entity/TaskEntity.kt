package com.example.todoapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todoapp.domain.model.ScheduleType
import java.time.Instant
import java.time.LocalDate

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val notes: String? = null,
    val scheduleType: ScheduleType,
    val scheduleRuleJson: String,
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val colorHex: String? = null,
    val isArchived: Boolean = false,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
