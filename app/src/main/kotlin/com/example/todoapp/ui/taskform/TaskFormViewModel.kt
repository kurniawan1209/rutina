package com.example.todoapp.ui.taskform

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.local.entity.TaskEntity
import com.example.todoapp.data.repository.TaskRepository
import com.example.todoapp.domain.model.ScheduleRule
import com.example.todoapp.domain.model.ScheduleRuleParser
import com.example.todoapp.domain.model.ScheduleType
import com.example.todoapp.domain.scheduler.RecurrenceCalculator
import com.example.todoapp.ui.components.parseIconKey
import java.time.DayOfWeek
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TaskFormUiState(
    val taskId: Long? = null,
    val title: String = "",
    val notes: String = "",
    val scheduleType: ScheduleType = ScheduleType.ONCE,
    // Once
    val onceDate: LocalDate = LocalDate.now(),
    // Daily
    val dailyInterval: Int = 1,
    // Weekly
    val weeklyDays: Set<DayOfWeek> = setOf(LocalDate.now().dayOfWeek),
    // Monthly
    val monthlyDay: Int = LocalDate.now().dayOfMonth.coerceIn(1, 31),
    // Selected dates
    val selectedDates: Set<LocalDate> = emptySet(),
    // Interval
    val intervalDays: Int = 3,
    val intervalAnchor: LocalDate = LocalDate.now(),
    // Active range
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate? = null,
    val hasEndDate: Boolean = false,
    val colorHex: String = "#3525CD",
    val selectedIcon: String = "fitness_center",
    val previewDates: List<LocalDate> = emptyList(),
    val titleError: String? = null,
    val isSaving: Boolean = false,
    val isLoaded: Boolean = false
)

class TaskFormViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: TaskRepository = TaskRepository.getInstance(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(TaskFormUiState())
    val uiState: StateFlow<TaskFormUiState> = _uiState.asStateFlow()

    fun loadTask(taskId: Long?) {
        if (taskId == null || taskId <= 0) {
            _uiState.update { it.copy(isLoaded = true) }
            updatePreview()
            return
        }

        viewModelScope.launch {
            val task = repository.getTaskById(taskId)
            if (task != null) {
                val rule = ScheduleRuleParser.fromJson(task.scheduleType, task.scheduleRuleJson)
                val rawHex = task.colorHex ?: "#3525CD"
                val baseHex = rawHex.split(":").first()
                val iconKey = parseIconKey(rawHex)

                _uiState.update { state ->
                    var s = state.copy(
                        taskId = task.id,
                        title = task.title,
                        notes = task.notes ?: "",
                        scheduleType = task.scheduleType,
                        startDate = task.startDate,
                        endDate = task.endDate,
                        hasEndDate = task.endDate != null,
                        colorHex = baseHex,
                        selectedIcon = iconKey,
                        isLoaded = true
                    )
                    when (rule) {
                        is ScheduleRule.Once -> s = s.copy(onceDate = rule.date)
                        is ScheduleRule.Daily -> s = s.copy(dailyInterval = rule.intervalDays)
                        is ScheduleRule.Weekly -> s = s.copy(weeklyDays = rule.daysOfWeek)
                        is ScheduleRule.Monthly -> s = s.copy(monthlyDay = rule.dayOfMonth)
                        is ScheduleRule.SelectedDates -> s = s.copy(selectedDates = rule.dates)
                        is ScheduleRule.EveryNDays -> s = s.copy(
                            intervalDays = rule.intervalDays,
                            intervalAnchor = rule.anchorDate
                        )
                    }
                    s
                }
                updatePreview()
            }
        }
    }

    fun onTitleChanged(title: String) {
        _uiState.update { it.copy(title = title, titleError = null) }
    }

    fun onNotesChanged(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun onScheduleTypeChanged(type: ScheduleType) {
        _uiState.update { it.copy(scheduleType = type) }
        updatePreview()
    }

    fun onOnceDateChanged(date: LocalDate) {
        _uiState.update { it.copy(onceDate = date, startDate = date) }
        updatePreview()
    }

    fun onDailyIntervalChanged(interval: Int) {
        _uiState.update { it.copy(dailyInterval = interval.coerceAtLeast(1)) }
        updatePreview()
    }

    fun onWeeklyDayToggled(day: DayOfWeek) {
        _uiState.update { state ->
            val updated = state.weeklyDays.toMutableSet()
            if (updated.contains(day)) {
                if (updated.size > 1) updated.remove(day)
            } else {
                updated.add(day)
            }
            state.copy(weeklyDays = updated)
        }
        updatePreview()
    }

    fun onMonthlyDayChanged(day: Int) {
        _uiState.update { it.copy(monthlyDay = day.coerceIn(1, 31)) }
        updatePreview()
    }

    fun onSelectedDateAdded(date: LocalDate) {
        _uiState.update { state ->
            val updated = state.selectedDates.toMutableSet()
            if (updated.contains(date)) updated.remove(date) else updated.add(date)
            state.copy(selectedDates = updated)
        }
        updatePreview()
    }

    fun onIntervalDaysChanged(days: Int) {
        _uiState.update { it.copy(intervalDays = days.coerceAtLeast(1)) }
        updatePreview()
    }

    fun onIntervalAnchorChanged(date: LocalDate) {
        _uiState.update { it.copy(intervalAnchor = date) }
        updatePreview()
    }

    fun onStartDateChanged(date: LocalDate) {
        _uiState.update { state ->
            val newEnd = if (state.hasEndDate && state.endDate != null && state.endDate.isBefore(date)) date else state.endDate
            state.copy(startDate = date, endDate = newEnd)
        }
        updatePreview()
    }

    fun onHasEndDateChanged(hasEnd: Boolean) {
        _uiState.update { state ->
            val end = if (hasEnd) state.endDate ?: state.startDate.plusMonths(1) else null
            state.copy(hasEndDate = hasEnd, endDate = end)
        }
        updatePreview()
    }

    fun onEndDateChanged(date: LocalDate) {
        _uiState.update { state ->
            val safeDate = if (date.isBefore(state.startDate)) state.startDate else date
            state.copy(endDate = safeDate, hasEndDate = true)
        }
        updatePreview()
    }

    fun onColorChanged(colorHex: String) {
        val baseHex = colorHex.split(":").first()
        _uiState.update { it.copy(colorHex = baseHex) }
    }

    fun onIconChanged(iconKey: String) {
        _uiState.update { it.copy(selectedIcon = iconKey) }
    }

    private fun getCurrentRule(): ScheduleRule {
        val s = _uiState.value
        return when (s.scheduleType) {
            ScheduleType.ONCE -> ScheduleRule.Once(s.onceDate)
            ScheduleType.DAILY -> ScheduleRule.Daily(s.dailyInterval)
            ScheduleType.WEEKLY -> ScheduleRule.Weekly(s.weeklyDays)
            ScheduleType.MONTHLY -> ScheduleRule.Monthly(s.monthlyDay)
            ScheduleType.SELECTED_DATES -> ScheduleRule.SelectedDates(s.selectedDates)
            ScheduleType.INTERVAL -> ScheduleRule.EveryNDays(s.intervalDays, s.intervalAnchor)
        }
    }

    private fun updatePreview() {
        val s = _uiState.value
        val rule = getCurrentRule()
        val today = LocalDate.now()
        val preview = RecurrenceCalculator.calculateOccurrences(
            rule = rule,
            startDate = s.startDate,
            endDate = if (s.hasEndDate) s.endDate else null,
            windowStart = today,
            windowEnd = today.plusDays(90)
        ).take(5)

        _uiState.update { it.copy(previewDates = preview) }
    }

    fun saveTask(onSuccess: () -> Unit) {
        val s = _uiState.value
        if (s.title.trim().isBlank()) {
            _uiState.update { it.copy(titleError = "Title cannot be empty") }
            return
        }

        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val rule = getCurrentRule()
            val combinedColorHex = "${s.colorHex}:${s.selectedIcon}"
            val entity = TaskEntity(
                id = s.taskId ?: 0L,
                title = s.title.trim(),
                notes = s.notes.trim().ifEmpty { null },
                scheduleType = rule.type,
                scheduleRuleJson = ScheduleRuleParser.toJson(rule),
                startDate = s.startDate,
                endDate = if (s.hasEndDate) s.endDate else null,
                colorHex = combinedColorHex,
                isArchived = false
            )
            repository.saveTask(entity, rule)
            _uiState.update { it.copy(isSaving = false) }
            onSuccess()
        }
    }
}
