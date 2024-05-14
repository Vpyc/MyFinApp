package com.example.myfinapp.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "operation",
    indices = [Index("id")],
    foreignKeys = [
        ForeignKey(
            entity = CardEntity::class,
            parentColumns = ["id"],
            childColumns = ["card_id"]
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"]
        )
    ]
)
data class OperationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "sum") val sum: Double,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "income") val income: Boolean,
    @ColumnInfo(name = "description") val description: String = "",
    @ColumnInfo(name = "card_id") val cardId: Long,
    @ColumnInfo(name = "category_id") val categoryId: Long,
)
