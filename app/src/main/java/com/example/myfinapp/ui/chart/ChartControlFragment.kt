package com.example.myfinapp.ui.chart

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myfinapp.R
import com.example.myfinapp.databinding.FragmentChartControlBinding
import kotlinx.coroutines.launch

class ChartControlFragment : Fragment() {

    private var _binding: FragmentChartControlBinding? = null
    private lateinit var chartControlViewModel: ChartControlViewModel

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        chartControlViewModel = ViewModelProvider(
            this, ChartControlViewModelFactory(requireContext())
        )[ChartControlViewModel::class.java]
        _binding = FragmentChartControlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            chartControlViewModel.mcsList.collect { mcs ->
                val donutChartFragment = DonutChartFragment()
                val bundle = Bundle()
                bundle.putParcelableArrayList("mcs", ArrayList(mcs))
                donutChartFragment.arguments = bundle
                childFragmentManager.beginTransaction()
                    .replace(R.id.chart_fragment, donutChartFragment)
                    .commit()
            }
        }
        binding.monthPicker.setOnClickListener {
            showDatePickerDialog()
        }

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
            date = ("%02d".format(month + 1) + "." + "%02d".format(year))
        }

        buttonCancel.setOnClickListener { dialog.dismiss() }

        buttonOk.setOnClickListener {
            val editTextDate = binding.monthPicker
            editTextDate.setText(date)
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        chartControlViewModel.getMcs()
    }
}