package com.example.todoapp.domain

import com.example.todoapp.domain.model.ScheduleRule
import com.example.todoapp.domain.model.ScheduleRuleParser
import com.example.todoapp.domain.model.ScheduleType
import com.example.todoapp.domain.scheduler.RecurrenceCalculator
import java.time.DayOfWeek
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RecurrenceCalculatorTest {

    @Test
    fun testOnceSchedule() {
        val date = LocalDate.of(2026, 11, 20)
        val rule = ScheduleRule.Once(date)
        val dates = RecurrenceCalculator.calculateOccurrences(
            rule = rule,
            startDate = LocalDate.of(2026, 11, 1),
            endDate = null,
            windowStart = LocalDate.of(2026, 11, 1),
            windowEnd = LocalDate.of(2026, 11, 30)
        )
        assertEquals(listOf(date), dates)
    }

    @Test
    fun testDailySchedule() {
        val start = LocalDate.of(2026, 11, 14)
        val rule = ScheduleRule.Daily(intervalDays = 1)
        val dates = RecurrenceCalculator.calculateOccurrences(
            rule = rule,
            startDate = start,
            endDate = null,
            windowStart = LocalDate.of(2026, 11, 14),
            windowEnd = LocalDate.of(2026, 11, 17)
        )
        assertEquals(
            listOf(
                LocalDate.of(2026, 11, 14),
                LocalDate.of(2026, 11, 15),
                LocalDate.of(2026, 11, 16),
                LocalDate.of(2026, 11, 17)
            ),
            dates
        )
    }

    @Test
    fun testWeeklySchedule() {
        val start = LocalDate.of(2026, 11, 16) // Monday
        val rule = ScheduleRule.Weekly(setOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY))
        val dates = RecurrenceCalculator.calculateOccurrences(
            rule = rule,
            startDate = start,
            endDate = null,
            windowStart = LocalDate.of(2026, 11, 16),
            windowEnd = LocalDate.of(2026, 11, 29)
        )
        // Mon 16, Fri 20, Mon 23, Fri 27
        assertEquals(
            listOf(
                LocalDate.of(2026, 11, 16),
                LocalDate.of(2026, 11, 20),
                LocalDate.of(2026, 11, 23),
                LocalDate.of(2026, 11, 27)
            ),
            dates
        )
    }

    @Test
    fun testMonthlyOn31stSkipsShorterMonths() {
        val start = LocalDate.of(2026, 1, 1)
        val rule = ScheduleRule.Monthly(dayOfMonth = 31)
        val dates = RecurrenceCalculator.calculateOccurrences(
            rule = rule,
            startDate = start,
            endDate = null,
            windowStart = LocalDate.of(2026, 1, 1),
            windowEnd = LocalDate.of(2026, 6, 30)
        )
        // Jan has 31
        // Feb has 28 (skips)
        // Mar has 31
        // Apr has 30 (skips)
        // May has 31
        // Jun has 30 (skips)
        assertEquals(
            listOf(
                LocalDate.of(2026, 1, 31),
                LocalDate.of(2026, 3, 31),
                LocalDate.of(2026, 5, 31)
            ),
            dates
        )
    }

    @Test
    fun testSelectedDates() {
        val start = LocalDate.of(2026, 11, 1)
        val chosen = setOf(
            LocalDate.of(2026, 11, 20),
            LocalDate.of(2026, 11, 25),
            LocalDate.of(2026, 12, 2)
        )
        val rule = ScheduleRule.SelectedDates(chosen)
        val dates = RecurrenceCalculator.calculateOccurrences(
            rule = rule,
            startDate = start,
            endDate = LocalDate.of(2026, 11, 30),
            windowStart = LocalDate.of(2026, 11, 1),
            windowEnd = LocalDate.of(2026, 12, 31)
        )
        // Dec 2 excluded by endDate
        assertEquals(
            listOf(
                LocalDate.of(2026, 11, 20),
                LocalDate.of(2026, 11, 25)
            ),
            dates
        )
    }

    @Test
    fun testEveryNDaysSchedule() {
        val anchor = LocalDate.of(2026, 11, 14)
        val rule = ScheduleRule.EveryNDays(intervalDays = 3, anchorDate = anchor)
        val dates = RecurrenceCalculator.calculateOccurrences(
            rule = rule,
            startDate = anchor,
            endDate = null,
            windowStart = LocalDate.of(2026, 11, 14),
            windowEnd = LocalDate.of(2026, 11, 24)
        )
        // 14, 17, 20, 23
        assertEquals(
            listOf(
                LocalDate.of(2026, 11, 14),
                LocalDate.of(2026, 11, 17),
                LocalDate.of(2026, 11, 20),
                LocalDate.of(2026, 11, 23)
            ),
            dates
        )
    }

    @Test
    fun testEndDateIsInclusive() {
        val start = LocalDate.of(2026, 11, 1)
        val end = LocalDate.of(2026, 11, 5)
        val rule = ScheduleRule.Daily(intervalDays = 1)
        val dates = RecurrenceCalculator.calculateOccurrences(
            rule = rule,
            startDate = start,
            endDate = end,
            windowStart = LocalDate.of(2026, 11, 1),
            windowEnd = LocalDate.of(2026, 11, 10)
        )
        assertEquals(5, dates.size)
        assertEquals(LocalDate.of(2026, 11, 5), dates.last())
    }

    @Test
    fun testScheduleRuleParserRoundTrip() {
        val weekly = ScheduleRule.Weekly(setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY))
        val json = ScheduleRuleParser.toJson(weekly)
        val parsed = ScheduleRuleParser.fromJson(ScheduleType.WEEKLY, json)
        assertTrue(parsed is ScheduleRule.Weekly)
        assertEquals(setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY), (parsed as ScheduleRule.Weekly).daysOfWeek)
    }
}
