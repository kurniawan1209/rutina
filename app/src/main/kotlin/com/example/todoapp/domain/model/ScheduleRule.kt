package com.example.todoapp.domain.model

import java.time.DayOfWeek
import java.time.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

enum class ScheduleType {
    ONCE,
    DAILY,
    WEEKLY,
    MONTHLY,
    SELECTED_DATES,
    INTERVAL
}

sealed interface ScheduleRule {
    val type: ScheduleType

    data class Once(val date: LocalDate) : ScheduleRule {
        override val type: ScheduleType = ScheduleType.ONCE
    }

    data class Daily(val intervalDays: Int = 1) : ScheduleRule {
        override val type: ScheduleType = ScheduleType.DAILY
    }

    data class Weekly(val daysOfWeek: Set<DayOfWeek>) : ScheduleRule {
        override val type: ScheduleType = ScheduleType.WEEKLY
    }

    data class Monthly(val dayOfMonth: Int) : ScheduleRule {
        override val type: ScheduleType = ScheduleType.MONTHLY
    }

    data class SelectedDates(val dates: Set<LocalDate>) : ScheduleRule {
        override val type: ScheduleType = ScheduleType.SELECTED_DATES
    }

    data class EveryNDays(val intervalDays: Int, val anchorDate: LocalDate) : ScheduleRule {
        override val type: ScheduleType = ScheduleType.INTERVAL
    }
}

@Serializable
private data class ScheduleRuleDto(
    val type: String,
    val date: String? = null,
    val intervalDays: Int? = null,
    val weekdays: List<String>? = null,
    val dayOfMonth: Int? = null,
    val dates: List<String>? = null,
    val anchorDate: String? = null
)

object ScheduleRuleParser {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun toJson(rule: ScheduleRule): String {
        val dto = when (rule) {
            is ScheduleRule.Once -> ScheduleRuleDto(
                type = rule.type.name,
                date = rule.date.toString()
            )
            is ScheduleRule.Daily -> ScheduleRuleDto(
                type = rule.type.name,
                intervalDays = rule.intervalDays
            )
            is ScheduleRule.Weekly -> ScheduleRuleDto(
                type = rule.type.name,
                weekdays = rule.daysOfWeek.sortedBy { it.value }.map { it.name }
            )
            is ScheduleRule.Monthly -> ScheduleRuleDto(
                type = rule.type.name,
                dayOfMonth = rule.dayOfMonth
            )
            is ScheduleRule.SelectedDates -> ScheduleRuleDto(
                type = rule.type.name,
                dates = rule.dates.sorted().map { it.toString() }
            )
            is ScheduleRule.EveryNDays -> ScheduleRuleDto(
                type = rule.type.name,
                intervalDays = rule.intervalDays,
                anchorDate = rule.anchorDate.toString()
            )
        }
        return json.encodeToString(ScheduleRuleDto.serializer(), dto)
    }

    fun fromJson(type: ScheduleType, jsonStr: String): ScheduleRule {
        if (jsonStr.isBlank()) {
            return defaultRuleFor(type)
        }
        return try {
            val dto = json.decodeFromString(ScheduleRuleDto.serializer(), jsonStr)
            when (type) {
                ScheduleType.ONCE -> {
                    val d = dto.date?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: LocalDate.now()
                    ScheduleRule.Once(d)
                }
                ScheduleType.DAILY -> {
                    val interval = (dto.intervalDays ?: 1).coerceAtLeast(1)
                    ScheduleRule.Daily(interval)
                }
                ScheduleType.WEEKLY -> {
                    val days = dto.weekdays?.mapNotNull { name ->
                        runCatching { DayOfWeek.valueOf(name) }.getOrNull()
                    }?.toSet()
                    ScheduleRule.Weekly(if (days.isNullOrEmpty()) setOf(LocalDate.now().dayOfWeek) else days)
                }
                ScheduleType.MONTHLY -> {
                    val day = (dto.dayOfMonth ?: 1).coerceIn(1, 31)
                    ScheduleRule.Monthly(day)
                }
                ScheduleType.SELECTED_DATES -> {
                    val dates = dto.dates?.mapNotNull { str ->
                        runCatching { LocalDate.parse(str) }.getOrNull()
                    }?.toSet() ?: emptySet()
                    ScheduleRule.SelectedDates(dates)
                }
                ScheduleType.INTERVAL -> {
                    val interval = (dto.intervalDays ?: 1).coerceAtLeast(1)
                    val anchor = dto.anchorDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: LocalDate.now()
                    ScheduleRule.EveryNDays(interval, anchor)
                }
            }
        } catch (_: Exception) {
            defaultRuleFor(type)
        }
    }

    private fun defaultRuleFor(type: ScheduleType): ScheduleRule {
        return when (type) {
            ScheduleType.ONCE -> ScheduleRule.Once(LocalDate.now())
            ScheduleType.DAILY -> ScheduleRule.Daily(1)
            ScheduleType.WEEKLY -> ScheduleRule.Weekly(setOf(LocalDate.now().dayOfWeek))
            ScheduleType.MONTHLY -> ScheduleRule.Monthly(1)
            ScheduleType.SELECTED_DATES -> ScheduleRule.SelectedDates(emptySet())
            ScheduleType.INTERVAL -> ScheduleRule.EveryNDays(1, LocalDate.now())
        }
    }
}
