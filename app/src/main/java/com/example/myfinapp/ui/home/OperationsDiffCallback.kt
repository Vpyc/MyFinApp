package com.example.myfinapp.ui.home

import androidx.recyclerview.widget.DiffUtil
import com.example.myfinapp.room.OperationEntity

class OperationsDiffCallback : DiffUtil.ItemCallback<OperationEntity>() {
    override fun areItemsTheSame(oldItem: OperationEntity, newItem: OperationEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: OperationEntity, newItem: OperationEntity): Boolean {
        return oldItem == newItem
    }
}