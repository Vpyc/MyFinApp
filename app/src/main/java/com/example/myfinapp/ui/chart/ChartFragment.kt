package com.example.myfinapp.ui.chart

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myfinapp.databinding.FragmentChartBinding
import com.example.myfinapp.room.McsItem
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.Locale

class ChartFragment : Fragment() {
    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mcs = arguments?.getParcelableArrayList<McsItem>("mcs")

        val barChart = binding.barChart

        val barData = mcs?.let { createBarData(it, barChart) }

        barChart.data = barData
        barChart.invalidate()
    }

    private fun createBarData(items: List<McsItem>, barChart: BarChart): BarData {
        val dateFormat = SimpleDateFormat("MM.yyyy", Locale.getDefault())

        val plusEntries = mutableListOf<BarEntry>()
        val minusEntries = mutableListOf<BarEntry>()

        val dates = items.map { it.formattedDate }
        val uniqueDates = dates.distinct()

        uniqueDates.forEachIndexed { index, date ->
            val plusValue = items.filter { it.formattedDate == date }.sumByDouble { it.plus }
            val minusValue = items.filter { it.formattedDate == date }.sumByDouble { it.minus }

            plusEntries.add(BarEntry(index.toFloat(), plusValue.toFloat()))
            minusEntries.add(BarEntry(index.toFloat(), minusValue.toFloat() * -1))
        }

        val plusDataSet = BarDataSet(plusEntries, "Плюсы")
        plusDataSet.color = Color.GREEN
        plusDataSet.setDrawValues(false)

        val minusDataSet = BarDataSet(minusEntries, "Минусы")
        minusDataSet.color = Color.RED
        minusDataSet.setDrawValues(false)

        val barWidth = 0.4f

        val barData = BarData(plusDataSet, minusDataSet)
        barData.barWidth = barWidth

        val groupCount = uniqueDates.size
        val startYear = 0

        barChart.xAxis.axisMinimum = startYear.toFloat()
        barChart.xAxis.axisMaximum = startYear + groupCount.toFloat()
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(uniqueDates)
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.setDrawGridLines(false)
        barChart.setDrawGridBackground(false)
        barChart.axisRight.isEnabled = false
        barChart.xAxis.setLabelCount(uniqueDates.size, true)

        return barData
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

}