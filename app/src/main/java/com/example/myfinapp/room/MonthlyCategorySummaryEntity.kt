package com.example.myfinapp.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    @ColumnInfo(name = "plus") var plus: Double,
    @ColumnInfo(name = "minus") var minus: Double,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "category_id") val categoryId: Long,
)
