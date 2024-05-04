package com.example.myfinapp.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.YearMonth
import java.util.Date

@Entity(
    tableName = "monthly_category_summary",
    indices = [Index("id")],
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"]
        )
    ]
)
data class MonthlyCategorySummaryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "plus") val plus: Double,
    @ColumnInfo(name = "minus") val minus: Double,
    @ColumnInfo(name = "date") val date: YearMonth,
    @ColumnInfo(name = "category_id") val categoryId: Long,
)
