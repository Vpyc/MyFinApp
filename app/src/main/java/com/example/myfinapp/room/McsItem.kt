package com.example.myfinapp.room

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
@DatabaseView(
    "SELECT mcs.id, mcs.plus, mcs.minus, c.category_name, " +
            "strftime('%m.%Y', mcs.date, 'unixepoch', 'localtime') AS formattedDate " +
            "FROM monthly_category_summary mcs " +
            "JOIN category c ON mcs.category_id = c.id " +
            "ORDER BY mcs.date DESC"
)
data class McsItem (
    val id: Long,
    val plus: Double,
    val minus: Double,
    @ColumnInfo(name = "category_name") val categoryName: String,
    val formattedDate: String,
)