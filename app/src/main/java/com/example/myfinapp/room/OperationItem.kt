package com.example.myfinapp.room

import androidx.room.ColumnInfo
import androidx.room.DatabaseView

@DatabaseView("SELECT o.id, o.sum, o.income, o.description, c.category_name, cd.card_number, " +
        "strftime('%d.%m.%Y %H:%M', o.date, 'unixepoch', 'localtime') AS formattedDate " +
        "FROM operation o " +
        "JOIN category c ON o.category_id = c.id " +
        "JOIN card cd ON o.card_id = cd.id " +
        "ORDER BY o.date DESC")
data class OperationItem(
    val id: Long,
    val sum: Double,
    val income: Boolean,
    val description: String,
    @ColumnInfo(name = "category_name")val categoryName: String,
    @ColumnInfo(name = "card_number") val cardNumber: String,
    val formattedDate: String
)
