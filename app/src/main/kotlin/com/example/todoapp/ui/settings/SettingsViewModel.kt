package com.example.todoapp.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.local.model.TaskWithOccurrence
import com.example.todoapp.data.preferences.AppThemeMode
import com.example.todoapp.data.preferences.UserPreferencesRepository
import com.example.todoapp.data.repository.TaskRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val widgetShowCompleted: Boolean = false,
    val todayOccurrences: List<TaskWithOccurrence> = emptyList()
)

class SettingsViewModel @JvmOverloads constructor(
    application: Application,
    private val preferencesRepository: UserPreferencesRepository = UserPreferencesRepository(application),
    private val taskRepository: TaskRepository = TaskRepository.getInstance(application)
) : AndroidViewModel(application) {

    val uiState: StateFlow<SettingsUiState> = combine(
        preferencesRepository.themeMode,
        preferencesRepository.widgetShowCompleted,
        taskRepository.observeTodayOccurrences(LocalDate.now())
    ) { theme, widgetShowCompleted, occurrences ->
        SettingsUiState(
            themeMode = theme,
            widgetShowCompleted = widgetShowCompleted,
            todayOccurrences = occurrences
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode)
        }
    }

    fun setWidgetShowCompleted(show: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setWidgetShowCompleted(show)
        }
    }

    fun toggleOccurrence(occurrenceId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            taskRepository.toggleOccurrenceStatus(occurrenceId, isCompleted)
        }
    }
}
