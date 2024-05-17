package com.example.myfinapp.ui.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myfinapp.Repository
import com.example.myfinapp.room.MyFinDb
import com.example.myfinapp.ui.home.DateConverter
import com.example.myfinapp.ui.home.HomeViewModel

class ChartControlViewModelFactory(context: Context): ViewModelProvider.Factory {
    private val db = MyFinDb.getDb(context)
    private val operationDao = db.getOperationDao()
    private val cardDao = db.getCardDao()
    private val categoryDao = db.getCategoryDao()
    private val mcsDao = db.getMCSDao()
    private val repository = Repository(db, cardDao, categoryDao, operationDao, mcsDao)
    private val converter = DateConverter()
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChartControlViewModel(repository) as T
    }
}