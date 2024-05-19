package com.example.myfinapp.ui.chart

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfinapp.Repository
import com.example.myfinapp.room.McsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChartControlViewModel(private val repository: Repository) : ViewModel() {

    private val _mcsList = MutableStateFlow(emptyList<McsItem>())
    val mcsList = _mcsList.asStateFlow()

    init {
        getOperations()
    }

    private fun getOperations() {
        viewModelScope.launch {
            repository.getAllMcsWithFormattedData().flowOn(Dispatchers.IO)
                .collect { mcs: List<McsItem> ->
                    Log.d("ChartControlViewModel", "Received MCS items: $mcs")
                    _mcsList.update { mcs }
                }
        }
    }
}