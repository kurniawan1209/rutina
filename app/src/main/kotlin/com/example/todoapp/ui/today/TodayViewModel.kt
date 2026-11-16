package com.example.todoapp.ui.today

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.local.entity.OccurrenceStatus
import com.example.todoapp.data.local.model.TaskWithOccurrence
import com.example.todoapp.data.repository.TaskRepository
import com.example.todoapp.widget.TodayWidget
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TodayUiState(
    val date: LocalDate = LocalDate.now(),
    val occurrences: List<TaskWithOccurrence> = emptyList(),
    val overdueOccurrences: List<TaskWithOccurrence> = emptyList(),
    val isLoading: Boolean = false
) {
    val completedCount: Int = occurrences.count { it.occurrence.status == OccurrenceStatus.COMPLETED }
    val totalCount: Int = occurrences.size
    val incompleteOccurrences: List<TaskWithOccurrence> =
        occurrences.filter { it.occurrence.status == OccurrenceStatus.PENDING }
    val completedOccurrences: List<TaskWithOccurrence> =
        occurrences.filter { it.occurrence.status == OccurrenceStatus.COMPLETED }
}

class TodayViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: TaskRepository = TaskRepository.getInstance(application)
) : AndroidViewModel(application) {

    private val today = LocalDate.now()

    val uiState: StateFlow<TodayUiState> = combine(
        repository.observeTodayOccurrences(today),
        repository.observeOverdueOccurrences(today)
    ) { todayList, overdueList ->
        TodayUiState(
            date = today,
            occurrences = todayList,
            overdueOccurrences = overdueList,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TodayUiState(date = today, isLoading = true)
    )

    init {
        viewModelScope.launch {
            repository.seedDefaultTrainingScheduleIfNotExists(today)
            repository.reconcileAllActiveTasks(today)
        }
    }

    fun toggleOccurrence(occurrenceId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleOccurrenceStatus(occurrenceId, isCompleted)
            TodayWidget.updateAllWidgets(getApplication())
        }
    }
}
