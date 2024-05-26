package com.example.myfinapp.ui.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import co.yml.charts.axis.AxisData
import co.yml.charts.axis.DataCategoryOptions
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarStyle
import com.example.myfinapp.R
import com.example.myfinapp.databinding.FragmentChartBinding
import com.example.myfinapp.room.McsItem

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
        val composeView = view.findViewById<ComposeView>(R.id.compose_view)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                if (!mcs.isNullOrEmpty()) {
                    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
                        Surface(
                            color = MaterialTheme.colorScheme.background, // Используем цвет фона из текущей темы
                        ) {
                            McsBarChart(mcs, "Расходы")
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun McsBarChart(mcs: ArrayList<McsItem>, category: String) {
        val chartData = when (category) {
            "Расходы" -> {
                createExpenseBarChartData(mcs)
            }

            "Доходы" -> {
                createIncomeBarChartData(mcs)
            }

            else -> {
                emptyList()
            }
        }
        val stepSize = 5
        val xAxisData = AxisData.Builder()
            .startDrawPadding(30.dp)
            .backgroundColor(Color.Transparent)
            .shouldDrawAxisLineTillEnd(true)
            .axisStepSize(30.dp)
            .endPadding(30.dp)
            .topPadding(30.dp)
            .steps(chartData.size - 1)
            .bottomPadding(40.dp)
            .axisLabelAngle(20f)
            .labelData { index -> chartData[index].label }
            .axisLineColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelColor(MaterialTheme.colorScheme.tertiary)
            .build()

        val maxRange = chartData.maxOf { it.point.y }

        val yAxisData = AxisData.Builder()
            .steps(stepSize)
            .backgroundColor(Color.Transparent)
            .labelAndAxisLinePadding(20.dp)
            .axisOffset(30.dp)
            .labelData { index ->
                (index * (maxRange / stepSize)).toInt().toString()
            } // Округляем до целых чисел
            .axisLineColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelColor(MaterialTheme.colorScheme.tertiary)
            .build()

        val barStyle = BarStyle(paddingBetweenBars = 20.dp, barWidth = 25.dp, cornerRadius = 5.dp)

        val barChartData = BarChartData(
            chartData = chartData,
            xAxisData = xAxisData,
            yAxisData = yAxisData,
            backgroundColor = MaterialTheme.colorScheme.surface,
            barStyle = barStyle
        )

        BarChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp),
            barChartData = barChartData
        )
    }

    private fun createIncomeBarChartData(mcs: ArrayList<McsItem>): List<BarData> {
        val incomeData = mutableListOf<BarData>()

        val uniqueDates = mcs.map { it.formattedDate.orEmpty() }.distinct()

        for ((index, date) in uniqueDates.withIndex()) {
            val dateItems = mcs.filter { it.formattedDate.orEmpty() == date }

            val totalPlus = dateItems.sumOf { it.plus }.toFloat()
            val color = Color(resources.getColor(R.color.green))

            incomeData.add(
                BarData(
                    point = Point(index.toFloat(), totalPlus),
                    color = color,
                    dataCategoryOptions = DataCategoryOptions(),
                    label = date
                )
            )
        }
        return incomeData
    }

    private fun createExpenseBarChartData(mcs: ArrayList<McsItem>): List<BarData> {
        val expenseData = mutableListOf<BarData>()

        val uniqueDates = mcs.map { it.formattedDate.orEmpty() }.distinct()

        for ((index, date) in uniqueDates.withIndex()) {
            val dateItems = mcs.filter { it.formattedDate.orEmpty() == date }

            val totalMinus = dateItems.sumOf { it.minus }.toFloat()
            val color = Color(resources.getColor(R.color.red))

            expenseData.add(
                BarData(
                    point = Point(index.toFloat(), totalMinus),
                    color = color,
                    dataCategoryOptions = DataCategoryOptions(),
                    label = date
                )
            )
        }
        return expenseData
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}