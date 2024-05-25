package com.example.myfinapp.ui.home

import androidx.recyclerview.widget.DiffUtil
import com.example.myfinapp.room.OperationItem

class OperationsDiffCallback : DiffUtil.ItemCallback<OperationGroup>() {
    override fun areItemsTheSame(oldItem: OperationGroup, newItem: OperationGroup): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: OperationGroup, newItem: OperationGroup): Boolean {
        return oldItem == newItem
    }
}