package com.example.myfinapp.ui.home

import androidx.recyclerview.widget.DiffUtil
import com.example.myfinapp.room.OperationItem

class OperationsDiffCallback : DiffUtil.ItemCallback<OperationItem>() {
    override fun areItemsTheSame(oldItem: OperationItem, newItem: OperationItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: OperationItem, newItem: OperationItem): Boolean {
        return oldItem == newItem
    }
}