package com.example.myfinapp.room

import android.icu.text.SimpleDateFormat
import androidx.room.TypeConverter
import java.util.Locale

class DateConverter {
    private val dateFormatToUi = SimpleDateFormat(
        "dd.MM.yyyy HH:mm", Locale.getDefault()
    )
    private val dateFormatToDb = SimpleDateFormat(
        "yyyy-MM-dd HH:mm", Locale.getDefault()
    )

    @TypeConverter
    fun fromStringToTimestamp(date: String): String {
        val newDate = dateFormatToUi.parse(date)
        return dateFormatToDb.format(newDate)
    }

    @TypeConverter
    fun fromTimestampToString(timestamp: String): String {
        val newDate = dateFormatToDb.parse(timestamp)
        return dateFormatToUi.format(newDate)
    }
}

class YearMonthConverter {
    private val dateFormatToUi = SimpleDateFormat("MM.yyyy", Locale.getDefault())
    private val dateFormatToDb = SimpleDateFormat("yyyy-MM", Locale.getDefault())

    @TypeConverter
    fun fromTimestampToMyDate(timestamp: Long): String {
        return dateFormatToDb.format(timestamp * 1000L)
    }

    @TypeConverter
    fun fromYearMonthToDate(yearMonth: String): Long {
        val newYearMonth = dateFormatToUi.parse(yearMonth)
        return newYearMonth.time / 1000
    }

}