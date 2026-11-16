package com.example.todoapp.ui.taskdetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.local.entity.TaskEntity
import com.example.todoapp.data.local.entity.TaskOccurrenceEntity
import com.example.todoapp.data.repository.TaskRepository
import com.example.todoapp.domain.model.ScheduleRule
import com.example.todoapp.domain.model.ScheduleRuleParser
import com.example.todoapp.domain.scheduler.RecurrenceCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TaskDetailUiState(
    val task: TaskEntity? = null,
    val scheduleRule: ScheduleRule? = null,
    val scheduleSummary: String = "",
    val occurrences: List<TaskOccurrenceEntity> = emptyList(),
    val isLoading: Boolean = true
)

class TaskDetailViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: TaskRepository = TaskRepository.getInstance(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(TaskDetailUiState())
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    fun loadTask(taskId: Long) {
        viewModelScope.launch {
            val task = repository.getTaskById(taskId)
            if (task != null) {
                val rule = ScheduleRuleParser.fromJson(task.scheduleType, task.scheduleRuleJson)
                val summary = RecurrenceCalculator.formatScheduleSummary(rule)

                _uiState.update {
                    it.copy(
                        task = task,
                        scheduleRule = rule,
                        scheduleSummary = summary,
                        isLoading = false
                    )
                }

                repository.observeOccurrencesForTask(taskId).collect { occList ->
                    _uiState.update { it.copy(occurrences = occList) }
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun toggleOccurrence(occurrenceId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleOccurrenceStatus(occurrenceId, isCompleted)
        }
    }

    fun archiveTask(taskId: Long, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.setTaskArchived(taskId, true)
            onDone()
        }
    }

    fun deleteTask(taskId: Long, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
            onDone()
        }
    }
}
