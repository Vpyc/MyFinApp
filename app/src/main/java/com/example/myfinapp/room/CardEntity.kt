package com.example.myfinapp.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "card")
data class CardEntity (
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "card_number") val cardNumber: String
)