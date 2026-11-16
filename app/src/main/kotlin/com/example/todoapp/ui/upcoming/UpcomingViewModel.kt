package com.example.todoapp.ui.upcoming

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.local.model.TaskWithOccurrence
import com.example.todoapp.data.repository.TaskRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UpcomingUiState(
    val groupedOccurrences: Map<LocalDate, List<TaskWithOccurrence>> = emptyMap(),
    val overdueOccurrences: List<TaskWithOccurrence> = emptyList(),
    val selectedDate: LocalDate? = null,
    val isLoading: Boolean = false
)

class UpcomingViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: TaskRepository = TaskRepository.getInstance(application)
) : AndroidViewModel(application) {

    private val startDate = LocalDate.now().plusDays(1)
    private val endDate = LocalDate.now().plusDays(60)

    private val selectedDateFlow = MutableStateFlow<LocalDate?>(null)

    val uiState: StateFlow<UpcomingUiState> = combine(
        repository.observeUpcomingOccurrences(startDate, endDate),
        repository.observeOverdueOccurrences(LocalDate.now()),
        selectedDateFlow
    ) { upcomingList, overdueList, selectedDate ->
        val filteredList = if (selectedDate != null) {
            upcomingList.filter { it.occurrence.occurrenceDate == selectedDate }
        } else {
            upcomingList
        }

        val grouped = filteredList.groupBy { it.occurrence.occurrenceDate }.toSortedMap()
        UpcomingUiState(
            groupedOccurrences = grouped,
            overdueOccurrences = overdueList,
            selectedDate = selectedDate,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UpcomingUiState(isLoading = true)
    )

    fun selectDate(date: LocalDate?) {
        selectedDateFlow.value = if (selectedDateFlow.value == date) null else date
    }

    fun toggleOccurrence(occurrenceId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleOccurrenceStatus(occurrenceId, isCompleted)
        }
    }
}
