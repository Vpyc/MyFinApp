package com.example.myfinapp

import androidx.room.withTransaction
import com.example.myfinapp.room.CardDao
import com.example.myfinapp.room.CardEntity
import com.example.myfinapp.room.CategoryDao
import com.example.myfinapp.room.CategoryEntity
import com.example.myfinapp.room.MCSDao
import com.example.myfinapp.room.MonthlyCategorySummaryEntity
import com.example.myfinapp.room.MyFinDb
import com.example.myfinapp.room.OperationDao
import com.example.myfinapp.room.OperationEntity

class Repository(
    private val db: MyFinDb,
    private val cardDao: CardDao,
    private val categoryDao: CategoryDao,
    private val operationDao: OperationDao,
    private val mcsDao: MCSDao
) {
    suspend fun withTransaction(block: suspend () -> Unit) {
        db.withTransaction {
            block()
        }
    }

    suspend fun insertCard(card: CardEntity) = cardDao.insertCard(card)

    suspend fun findCardByCardNumber(cardNumber: String) = cardDao.findByCardNumber(cardNumber)

    suspend fun insertCategory(category: CategoryEntity) = categoryDao.insertCategory(category)

    suspend fun findCategoryByCategoryName(categoryName: String) =
        categoryDao.findByCategoryName(categoryName)

    suspend fun insertMcs(mcs: MonthlyCategorySummaryEntity) = mcsDao.insertMcs(mcs)

    suspend fun getAllMcs() = mcsDao.getAllMCS()

    suspend fun findMcsByDateAndCategoryId(date: Long, categoryId: Long) =
        mcsDao.findMcsByDateAndCategoryId(date, categoryId)

    suspend fun updateMcs(mcs: MonthlyCategorySummaryEntity) = mcsDao.updateMcs(mcs)

    suspend fun insertOperation(operation: OperationEntity) =
        operationDao.insertOperation(operation)

    fun getAllOperationsSortedByDate() = operationDao.getAllOperationsSortedByDate()
    fun getAllOperationsWithFormattedData() = operationDao.getAllOperationsWithFormattedData()

    suspend fun findOperationByAll(
        sum: Double,
        date: Long,
        income: Boolean,
        description: String,
        cardId: Long,
        categoryId: Long
    ) = operationDao.findOperationByAll(sum, date, income, description, cardId, categoryId)
}