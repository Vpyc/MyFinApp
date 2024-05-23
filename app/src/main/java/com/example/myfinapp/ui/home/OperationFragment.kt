package com.example.myfinapp.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myfinapp.R
import com.example.myfinapp.databinding.FragmentOperationBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import java.util.Calendar

class OperationFragment : BottomSheetDialogFragment() {
    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentOperationBinding? = null
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
        _binding = FragmentOperationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isButtonVisible = arguments?.getBoolean("isButtonVisible")
        if (isButtonVisible == false) {
            binding.buttonDelete.visibility = View.GONE
        }
        val buttonSave = binding.buttonSave
        val buttonDelete = binding.buttonDelete
        val editTextDate = binding.editTextDate
        // Проверяем видимость кнопок
        if (buttonDelete.visibility == View.GONE) {
            val params = buttonSave.layoutParams as ConstraintLayout.LayoutParams
            params.startToStart = ConstraintSet.PARENT_ID
            params.endToEnd = ConstraintSet.PARENT_ID
            params.topToBottom = binding.editTextComment.id
            params.horizontalBias = 0.5f
        }
        lifecycleScope.launch {
            homeViewModel.categoryList.collect { categories ->
                // Обновление адаптера Spinner с данными из categoryList
                val categoryNames = categories.map { it.categoryName }
                Log.d("OperationFragment", categoryNames.toString())
                val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
                binding.spinnerCategory.adapter = categoryAdapter
            }
        }
        lifecycleScope.launch {
            homeViewModel.cardList.collect { cards ->
                // Обновление адаптера Spinner с данными из cardList
                val cardNumbers = cards.map { it.cardNumber }
                Log.d("OperationFragment", cardNumbers.toString())
                val cardAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, cardNumbers)
                binding.spinnerCard.adapter = cardAdapter
            }
        }

        buttonSave.setOnClickListener {
            lifecycleScope.launch {
                val category = homeViewModel.findCategoryId(binding.spinnerCategory.selectedItem.toString())
                val card = homeViewModel.findCardId(binding.spinnerCard.selectedItem.toString())
                val date = pickDateWithTime(editTextDate.text.toString())
                val flag = getRadioButtonFlag()
                homeViewModel.insertOperation(
                    binding.editTextSum.text.toString(),
                    date,
                    flag,
                    binding.editTextComment.text.toString(),
                    card,
                    category
                )
            }
            findNavController().popBackStack()
        }

        editTextDate.setOnClickListener {
            showDatePickerDialog()
        }
    }
    private fun getRadioButtonFlag():Boolean {
        val radioGroup = binding.radioGroupType
        val radioButtonFlag = when (radioGroup.checkedRadioButtonId) {
            R.id.radioButtonIncome -> true
            R.id.radioButtonExpense -> false
            else -> false
        }
        return radioButtonFlag
    }
    private fun pickDateWithTime(date: String): Long {
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)
        val dateTimeString = "$date $hour:$minute"
        return homeViewModel.converter.convertOperationToLong(dateTimeString)
    }

    private fun showDatePickerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_date_picker, null)
        val calendarView = dialogView.findViewById<CalendarView>(R.id.calendarView)
        val buttonCancel = dialogView.findViewById<Button>(R.id.buttonCancel)
        val buttonOk = dialogView.findViewById<Button>(R.id.buttonOk)
        var date = ""

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        calendarView.setOnDateChangeListener { _, year, month, day ->
            date = ("%02d".format(day) + "." + "%02d".format(month + 1) + "." + "%02d".format(year))
        }

        buttonCancel.setOnClickListener { dialog.dismiss() }

        buttonOk.setOnClickListener {
            val editTextDate = binding.editTextDate
            editTextDate.setText(date)
            dialog.dismiss()
        }
        dialog.show()
    }
}