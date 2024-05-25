package com.example.myfinapp.ui.home

import android.graphics.Color
import android.view.View
import com.example.myfinapp.R
import com.example.myfinapp.databinding.ItemOperationBinding
import com.example.myfinapp.databinding.ItemOperationGroupBinding
import com.example.myfinapp.room.OperationItem
import com.xwray.groupie.viewbinding.BindableItem

class OperationGroupItem(private val operationGroup: OperationGroup) :
    BindableItem<ItemOperationGroupBinding>() {

    override fun bind(binding: ItemOperationGroupBinding, position: Int) {
        binding.textViewDate.text = operationGroup.date
    }

    override fun getLayout() = R.layout.item_operation_group
    override fun initializeViewBinding(view: View): ItemOperationGroupBinding {
        return ItemOperationGroupBinding.bind(view)
    }
}

class OperationViewItem(private val operationItem: OperationItem) :
    BindableItem<ItemOperationBinding>() {

    override fun bind(binding: ItemOperationBinding, position: Int) {
        val formattedTime = operationItem.formattedDate.substring(11)
        binding.textViewOperationDate.text = formattedTime
        binding.textViewOperationCategory.text = operationItem.categoryName
        binding.textViewOperationCard.text = operationItem.cardNumber
        val formattedSum = "${operationItem.sum.toString().replace(",", ".")} ₽"
        binding.textViewOperationSum.apply{
            text = formattedSum
            setTextColor(if (operationItem.income) Color.GREEN else Color.RED)
            text = if (operationItem.income) {
                "+ $text"
            } else {
                "- $text"
            }
        }
    }

    override fun getLayout() = R.layout.item_operation
    override fun initializeViewBinding(view: View): ItemOperationBinding {
        return ItemOperationBinding.bind(view)
    }
}

class OperationGroup(
    val date: String,
    val operations: List<OperationItem>
)