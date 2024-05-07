package com.example.myfinapp.room

import android.icu.text.SimpleDateFormat
import androidx.room.TypeConverter
import java.util.Locale

class DateConverter {
    private val dateFormatOperation = SimpleDateFormat("dd.MM.yyyy HH:mm"
                                        , Locale.getDefault())
    @TypeConverter
    fun fromStringToTimestamp(date: String): Long {
        val newDate = dateFormatOperation.parse(date)
        return newDate.time / 1000
    }

    @TypeConverter
    fun fromTimestampToString(timestamp: Long): String {
        return dateFormatOperation.format(timestamp * 1000L)
    }
}
class YearMonthConverter{
    private val dateFormatMCS = SimpleDateFormat("MM.yyyy", Locale.getDefault())

    @TypeConverter
    fun fromTimestampToMyDate(timestamp: Long): String {
        return dateFormatMCS.format(timestamp * 1000L)
    }

    @TypeConverter
    fun fromYearMonthToDate(yearMonth: String): Long {
        val newYearMonth = dateFormatMCS.parse(yearMonth)
        return newYearMonth.time / 1000
    }

}