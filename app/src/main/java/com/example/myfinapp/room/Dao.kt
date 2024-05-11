package com.example.myfinapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
@Dao
interface CardDao {
    @Insert(entity = CardEntity::class)
    suspend fun insertCard(card: CardEntity)
    @Query("SELECT * FROM card")
    fun getAllCards(): List<CardEntity>
}
@Dao
interface CategoryDao {
    @Insert(entity = CategoryEntity::class)
    suspend fun insertCategory(category: CategoryEntity)
    @Query("SELECT * FROM category")
    fun getAllCategories(): List<CategoryEntity>
}
@Dao
interface OperationDao{
    @Insert(entity = OperationEntity::class)
    suspend fun insertOperation(operation: OperationEntity)
    @Query("SELECT * FROM operation")
    fun getAllOperations(): List<OperationEntity>
}
@Dao
interface MCSDao {
    @Insert(entity = MonthlyCategorySummaryEntity::class)
    suspend fun insertMcs(mcs: MonthlyCategorySummaryEntity)
    @Query("SELECT * FROM monthly_category_summary")
    suspend fun getAllMCS(): List<MonthlyCategorySummaryEntity>
}

