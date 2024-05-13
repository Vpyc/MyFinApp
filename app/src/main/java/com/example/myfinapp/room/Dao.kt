package com.example.myfinapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CardDao {
    @Insert(entity = CardEntity::class)
    suspend fun insertCard(card: CardEntity): Long
    @Query("SELECT * FROM card")
    fun getAllCards(): List<CardEntity>
    @Query("SELECT id FROM card WHERE card_number = :name")
    suspend fun findByCardNumber(name: String): Long?
}
@Dao
interface CategoryDao {
    @Insert(entity = CategoryEntity::class)
    suspend fun insertCategory(category: CategoryEntity): Long
    @Query("SELECT * FROM category")
    fun getAllCategories(): List<CategoryEntity>
    @Query("SELECT id FROM category WHERE category_name = :name")
    suspend fun findByCategoryName(name: String): Long?
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
    @Update(entity = MonthlyCategorySummaryEntity::class)
    suspend fun updateMcs(mcs: MonthlyCategorySummaryEntity)
}

