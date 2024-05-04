package com.example.myfinapp.room

import androidx.room.TypeConverter
import java.time.YearMonth
import java.util.Calendar
import java.util.Date

class Converters {
    @TypeConverter
    fun fromDateToYearMonth(date: Date): YearMonth {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return YearMonth.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
    }

    @TypeConverter
    fun fromYearMonthToDate(yearMonth: YearMonth): Date {
        val calendar = Calendar.getInstance()
        calendar.set(yearMonth.year, yearMonth.monthValue - 1, 1)
        return calendar.time
    }
    @TypeConverter
    fun fromDateToTimestamp(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun fromTimestampToDate(timestamp: Long): Date {
        return Date(timestamp)
    }
}