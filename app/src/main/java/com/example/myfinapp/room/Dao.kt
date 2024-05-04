package com.example.myfinapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
@Dao
abstract class CardDao {
    @Insert(entity = CardEntity::class)
    abstract fun insertCard(card: CardEntity)
    @Query("SELECT * FROM card")
    abstract fun getAllCards(): List<CardEntity>
}
@Dao
abstract class CategoryDao {
    @Insert(entity = CategoryEntity::class)
    abstract fun insertCategory(category: CategoryEntity)
    @Query("SELECT * FROM category")
    abstract fun getAllCategories(): List<CategoryEntity>
}
@Dao
abstract class OperationDao{
    @Insert(entity = OperationEntity::class)
    abstract fun insertOperation(operation: OperationEntity)
    @Query("SELECT * FROM operation")
    abstract fun getAllOperations(): List<OperationEntity>
}
@Dao
abstract class MCSDao {
    @Query("SELECT * FROM monthly_category_summary")
    abstract fun getAllMCS(): List<MonthlyCategorySummaryEntity>
}

