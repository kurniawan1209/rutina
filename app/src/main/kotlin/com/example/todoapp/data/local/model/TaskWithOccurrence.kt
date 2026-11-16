package com.example.todoapp.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.todoapp.data.local.entity.TaskEntity
import com.example.todoapp.data.local.entity.TaskOccurrenceEntity

data class TaskWithOccurrence(
    @Embedded val occurrence: TaskOccurrenceEntity,
    @Relation(
        parentColumn = "taskId",
        entityColumn = "id"
    )
    val task: TaskEntity
)
