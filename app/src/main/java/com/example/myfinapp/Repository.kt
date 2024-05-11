package com.example.myfinapp

import com.example.myfinapp.room.CardDao
import com.example.myfinapp.room.CardEntity
import com.example.myfinapp.room.CategoryDao
import com.example.myfinapp.room.CategoryEntity
import com.example.myfinapp.room.MCSDao
import com.example.myfinapp.room.MonthlyCategorySummaryEntity
import com.example.myfinapp.room.OperationDao
import com.example.myfinapp.room.OperationEntity

class Repository(
    private val cardDao: CardDao,
    private val categoryDao: CategoryDao,
    private val operationDao: OperationDao,
    private val mcsDao: MCSDao
) {
    suspend fun insertCard(card: CardEntity) {
        cardDao.insertCard(card)
    }
    suspend fun insertCategory(category: CategoryEntity) {
        categoryDao.insertCategory(category)
    }

    suspend fun insertMcs(mcs: MonthlyCategorySummaryEntity) {
        mcsDao.insertMcs(mcs)
    }

    suspend fun getAllMcs(): List<MonthlyCategorySummaryEntity> {
        return mcsDao.getAllMCS()
    }

    suspend fun insertOperation(operation: OperationEntity) {
        operationDao.insertOperation(operation)
    }
}