package com.example.todoapp.data.local

import androidx.room.TypeConverter
import com.example.todoapp.data.local.entity.OccurrenceStatus
import com.example.todoapp.domain.model.ScheduleType
import java.time.Instant
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromInstant(instant: Instant?): Long? = instant?.toEpochMilli()

    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let { Instant.ofEpochMilli(it) }

    @TypeConverter
    fun fromScheduleType(type: ScheduleType?): String? = type?.name

    @TypeConverter
    fun toScheduleType(value: String?): ScheduleType? = value?.let { ScheduleType.valueOf(it) }

    @TypeConverter
    fun fromOccurrenceStatus(status: OccurrenceStatus?): String? = status?.name

    @TypeConverter
    fun toOccurrenceStatus(value: String?): OccurrenceStatus? = value?.let { OccurrenceStatus.valueOf(it) }
}
