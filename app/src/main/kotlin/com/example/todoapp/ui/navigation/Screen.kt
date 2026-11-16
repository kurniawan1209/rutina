package com.example.todoapp.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen : NavKey {
    @Serializable
    data object Today : Screen

    @Serializable
    data object Upcoming : Screen

    @Serializable
    data object AllTasks : Screen

    @Serializable
    data class TaskForm(val taskId: Long? = null) : Screen

    @Serializable
    data class TaskDetail(val taskId: Long) : Screen

    @Serializable
    data object Settings : Screen
}
