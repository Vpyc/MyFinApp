package com.example.myfinapp.ui.home

import android.icu.text.SimpleDateFormat
import java.util.Locale

class DateConverter {
    private val operationDateFormat = SimpleDateFormat(
        "dd.MM.yyyy HH:mm", Locale.getDefault()
    )
    private val mcsDateFormat = SimpleDateFormat(
        "MM.yyyy", Locale.getDefault()
    )

    fun convertOperationToLong(date: String): Long {
        val newDate = operationDateFormat.parse(date)
        return newDate.time / 1000
    }

    fun convertMcsToLong(date: String): Long {
        val newDate = mcsDateFormat.parse(date)
        return newDate.time / 1000
    }

    fun convertOperationFromLong(date: Long): String {
        return operationDateFormat.format(date * 1000)
    }

    fun convertMcsFromLong(date: Long): String {
        return mcsDateFormat.format(date * 1000L)
    }
}