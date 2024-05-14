package com.example.myfinapp.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CardEntity::class,
        CategoryEntity::class,
        OperationEntity::class,
        MonthlyCategorySummaryEntity::class,
    ],
    version = 1
)
abstract class MyFinDb : RoomDatabase() {
    abstract fun getCardDao(): CardDao
    abstract fun getCategoryDao(): CategoryDao
    abstract fun getOperationDao(): OperationDao
    abstract fun getMCSDao(): MCSDao

    companion object {
        fun getDb(context: Context): MyFinDb {
            return Room.databaseBuilder(
                context.applicationContext,
                MyFinDb::class.java,
                name = "test.db"
            ).build()
        }
    }
}
