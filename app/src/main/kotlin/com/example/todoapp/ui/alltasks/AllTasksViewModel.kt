package com.example.todoapp.ui.alltasks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.local.entity.TaskEntity
import com.example.todoapp.data.repository.TaskRepository
import com.example.todoapp.domain.model.ScheduleType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TaskFilterOption {
    ALL,
    RECURRING,
    ONCE,
    ARCHIVED
}

data class AllTasksUiState(
    val activeTasks: List<TaskEntity> = emptyList(),
    val archivedTasks: List<TaskEntity> = emptyList(),
    val filteredTasks: List<TaskEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: TaskFilterOption = TaskFilterOption.ALL,
    val showArchived: Boolean = false,
    val isLoading: Boolean = false
)

class AllTasksViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: TaskRepository = TaskRepository.getInstance(application)
) : AndroidViewModel(application) {

    private val showArchivedFlow = MutableStateFlow(false)
    private val searchQueryFlow = MutableStateFlow("")
    private val selectedFilterFlow = MutableStateFlow(TaskFilterOption.ALL)

    val uiState: StateFlow<AllTasksUiState> = combine(
        repository.observeActiveTasks(),
        repository.observeArchivedTasks(),
        searchQueryFlow,
        selectedFilterFlow
    ) { active, archived, query, filter ->
        val currentPool = if (filter == TaskFilterOption.ARCHIVED) archived else active

        val byCategory = when (filter) {
            TaskFilterOption.ALL -> currentPool
            TaskFilterOption.RECURRING -> currentPool.filter { it.scheduleType != ScheduleType.ONCE }
            TaskFilterOption.ONCE -> currentPool.filter { it.scheduleType == ScheduleType.ONCE }
            TaskFilterOption.ARCHIVED -> currentPool
        }

        val filtered = if (query.isBlank()) {
            byCategory
        } else {
            byCategory.filter {
                it.title.contains(query, ignoreCase = true) ||
                        (it.notes?.contains(query, ignoreCase = true) == true)
            }
        }

        AllTasksUiState(
            activeTasks = active,
            archivedTasks = archived,
            filteredTasks = filtered,
            searchQuery = query,
            selectedFilter = filter,
            showArchived = filter == TaskFilterOption.ARCHIVED,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AllTasksUiState(isLoading = true)
    )

    fun onSearchQueryChanged(query: String) {
        searchQueryFlow.value = query
    }

    fun onFilterSelected(filter: TaskFilterOption) {
        selectedFilterFlow.value = filter
        showArchivedFlow.value = (filter == TaskFilterOption.ARCHIVED)
    }

    fun setShowArchived(show: Boolean) {
        selectedFilterFlow.value = if (show) TaskFilterOption.ARCHIVED else TaskFilterOption.ALL
        showArchivedFlow.value = show
    }

    fun setTaskArchived(taskId: Long, isArchived: Boolean) {
        viewModelScope.launch {
            repository.setTaskArchived(taskId, isArchived)
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }

    fun loadTrainingSchedule(onResult: (Int) -> Unit = {}) {
        viewModelScope.launch {
            val count = repository.loadTrainingSchedule()
            onResult(count)
        }
    }
}
