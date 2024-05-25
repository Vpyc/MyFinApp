package com.example.myfinapp.ui.home

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myfinapp.databinding.DialogFilterBinding
import kotlinx.coroutines.launch

class FilterFragment: DialogFragment() {
    private lateinit var homeViewModel: HomeViewModel
    private var _binding: DialogFilterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(requireContext())
        )[HomeViewModel::class.java]
        _binding = DialogFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            homeViewModel.categoryList.collect { categories ->
                // Обновление адаптера Spinner с данными из categoryList
                val categoryNames = listOf("Все") + categories.map { it.categoryName }
                val categoryAdapter = ArrayAdapter(
                    requireContext(),
                    R.layout.simple_spinner_item,
                    categoryNames
                )
                binding.categorySpinner.adapter = categoryAdapter
            }
        }
        lifecycleScope.launch {
            homeViewModel.cardList.collect { cards ->
                // Обновление адаптера Spinner с данными из cardList
                val cardNumbers = listOf("Все") + cards.map { it.cardNumber }
                val cardAdapter = ArrayAdapter(
                    requireContext(),
                    R.layout.simple_spinner_item,
                    cardNumbers
                )
                binding.cardSpinner.adapter = cardAdapter
            }
        }
        binding.applyButton.setOnClickListener {
            val selectedCategory = binding.categorySpinner.selectedItem as String
            val selectedCard = binding.cardSpinner.selectedItem as String
            val selectedIncome = when {
                binding.incomeRadioButton.isChecked -> true
                binding.expenseRadioButton.isChecked -> false
                else -> null
            }

            homeViewModel.updateSelectedCategory(selectedCategory)
            homeViewModel.updateSelectedCard(selectedCard)
            homeViewModel.updateSelectedIncome(selectedIncome)

            dismiss()
        }
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}