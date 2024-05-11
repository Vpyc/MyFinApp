package com.example.myfinapp.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class CategoryEntity (
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "category_name") val categoryName: String,
    @ColumnInfo(name = "priority") val priority: Boolean = false
)
