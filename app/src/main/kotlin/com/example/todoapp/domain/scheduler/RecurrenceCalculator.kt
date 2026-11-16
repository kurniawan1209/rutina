package com.example.todoapp.domain.scheduler

import com.example.todoapp.domain.model.ScheduleRule
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

object RecurrenceCalculator {

    /**
     * Calculates all occurrence dates for a given schedule rule within [windowStart, windowEnd].
     * Both window bounds and [startDate, endDate] are inclusive.
     */
    fun calculateOccurrences(
        rule: ScheduleRule,
        startDate: LocalDate,
        endDate: LocalDate?,
        windowStart: LocalDate,
        windowEnd: LocalDate
    ): List<LocalDate> {
        val effectiveStart = maxOf(startDate, windowStart)
        val effectiveEnd = if (endDate != null) minOf(endDate, windowEnd) else windowEnd

        if (effectiveStart > effectiveEnd) {
            return emptyList()
        }

        return when (rule) {
            is ScheduleRule.Once -> {
                val targetDate = rule.date
                if (targetDate in effectiveStart..effectiveEnd &&
                    targetDate >= startDate &&
                    (endDate == null || targetDate <= endDate)
                ) {
                    listOf(targetDate)
                } else {
                    emptyList()
                }
            }

            is ScheduleRule.Daily -> {
                val interval = rule.intervalDays.coerceAtLeast(1)
                val results = mutableListOf<LocalDate>()
                var current = startDate

                // Advance current to at or after effectiveStart
                if (current < effectiveStart) {
                    val daysBetween = ChronoUnit.DAYS.between(current, effectiveStart)
                    val steps = (daysBetween + interval - 1) / interval
                    current = current.plusDays(steps * interval)
                }

                while (current <= effectiveEnd) {
                    if (current >= effectiveStart) {
                        results.add(current)
                    }
                    current = current.plusDays(interval.toLong())
                }
                results
            }

            is ScheduleRule.Weekly -> {
                if (rule.daysOfWeek.isEmpty()) return emptyList()
                val results = mutableListOf<LocalDate>()
                var current = effectiveStart
                while (current <= effectiveEnd) {
                    if (current.dayOfWeek in rule.daysOfWeek) {
                        results.add(current)
                    }
                    current = current.plusDays(1)
                }
                results
            }

            is ScheduleRule.Monthly -> {
                val targetDay = rule.dayOfMonth.coerceIn(1, 31)
                val results = mutableListOf<LocalDate>()
                var currentMonth = YearMonth.from(effectiveStart)
                val endMonth = YearMonth.from(effectiveEnd)

                while (!currentMonth.isAfter(endMonth)) {
                    val lengthOfMonth = currentMonth.lengthOfMonth()
                    // PRD requirement: If month does not contain the day (e.g. 31st), skip the month
                    if (targetDay <= lengthOfMonth) {
                        val date = currentMonth.atDay(targetDay)
                        if (date in effectiveStart..effectiveEnd) {
                            results.add(date)
                        }
                    }
                    currentMonth = currentMonth.plusMonths(1)
                }
                results
            }

            is ScheduleRule.SelectedDates -> {
                rule.dates
                    .filter { it in effectiveStart..effectiveEnd && it >= startDate && (endDate == null || it <= endDate) }
                    .sorted()
            }

            is ScheduleRule.EveryNDays -> {
                val interval = rule.intervalDays.coerceAtLeast(1)
                val anchor = rule.anchorDate
                val results = mutableListOf<LocalDate>()

                // Occurrences follow: anchor + k * interval, for integer k >= 0 (or from startDate onwards)
                // First valid occurrence >= startDate
                var first = anchor
                if (first < startDate) {
                    val daysDiff = ChronoUnit.DAYS.between(first, startDate)
                    val steps = (daysDiff + interval - 1) / interval
                    first = first.plusDays(steps * interval)
                }

                var current = first
                if (current < effectiveStart) {
                    val daysDiff = ChronoUnit.DAYS.between(current, effectiveStart)
                    val steps = (daysDiff + interval - 1) / interval
                    current = current.plusDays(steps * interval)
                }

                while (current <= effectiveEnd) {
                    if (current >= effectiveStart && current >= startDate) {
                        results.add(current)
                    }
                    current = current.plusDays(interval.toLong())
                }
                results
            }
        }
    }

    /**
     * Produces a human-readable summary of the schedule rule.
     */
    fun formatScheduleSummary(rule: ScheduleRule): String {
        return when (rule) {
            is ScheduleRule.Once -> "Once on ${rule.date}"
            is ScheduleRule.Daily -> if (rule.intervalDays == 1) "Daily" else "Every ${rule.intervalDays} days"
            is ScheduleRule.Weekly -> {
                val dayNames = rule.daysOfWeek.sortedBy { it.value }.joinToString(", ") {
                    it.name.take(3).lowercase().replaceFirstChar { c -> c.uppercase() }
                }
                "Weekly ($dayNames)"
            }
            is ScheduleRule.Monthly -> "Monthly on day ${rule.dayOfMonth}"
            is ScheduleRule.SelectedDates -> "${rule.dates.size} selected dates"
            is ScheduleRule.EveryNDays -> "Every ${rule.intervalDays} days"
        }
    }
}
