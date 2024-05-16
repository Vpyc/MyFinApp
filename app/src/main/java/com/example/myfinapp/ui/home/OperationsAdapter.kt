package com.example.myfinapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinapp.databinding.ItemOperationBinding
import com.example.myfinapp.room.OperationEntity

class OperationsAdapter :
    ListAdapter<OperationEntity, OperationsAdapter.OperationViewHolder>(OperationsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperationViewHolder {
        val binding =
            ItemOperationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OperationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OperationViewHolder, position: Int) {
        val operation = getItem(position)
        holder.bind(operation)
    }

    class OperationViewHolder(private val binding: ItemOperationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(operation: OperationEntity) {
            binding.textViewOperationDate.text = operation.date.toString()
            binding.textViewOperationSum.text = operation.sum.toString()
            // Здесь вы можете настроить отображение других полей операции
        }
    }
}