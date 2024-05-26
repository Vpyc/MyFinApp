package com.example.myfinapp.ui.chart

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import co.yml.charts.common.components.Legends
import co.yml.charts.common.model.PlotType
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.example.myfinapp.R
import com.example.myfinapp.databinding.FragmentDonutChartBinding
import com.example.myfinapp.room.McsItem
import kotlin.random.Random

class DonutChartFragment : Fragment() {
    private var _binding: FragmentDonutChartBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDonutChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получите список McsItem из аргументов фрагмента
        val mcs = arguments?.getParcelableArrayList<McsItem>("mcs")

        val composeView = view.findViewById<ComposeView>(R.id.donut_chart)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                if (!mcs.isNullOrEmpty()) {
                    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
                        Surface(
                            color = MaterialTheme.colorScheme.background, // Используем цвет фона из текущей темы
                        ) {
                            val data = filterAndPrepareDonutChartData(mcs, "05.2024")
                            if (data.isNotEmpty()) {
                                DonutChart(data)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun filterAndPrepareDonutChartData(
        mcs: List<McsItem>,
        month: String
    ): List<PieChartData.Slice> {
        val filteredData = mcs.filter { it.formattedDate == month && it.minus > 0 }

        val totalValue = filteredData.sumByDouble { it.minus }

        return filteredData.map { item ->
            val value = item.minus
            val percentage = (value / totalValue) * 100
            PieChartData.Slice(item.categoryName ?: "", percentage.toFloat(), getRandomColor())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Composable
    fun DonutChart(data: List<PieChartData.Slice>) {
        val donutChartData = PieChartData(slices = data, plotType = PlotType.Donut)

        val donutChartConfig = PieChartConfig(
            showSliceLabels = true,
            sliceLabelTextColor = Color.Black,
            sliceLabelTextSize = 28.sp,
            labelType = PieChartConfig.LabelType.PERCENTAGE,
            isEllipsizeEnabled = true,
            sliceLabelEllipsizeAt = TextUtils.TruncateAt.MIDDLE,
            chartPadding = 10,
            labelFontSize = 28.sp,
            labelColor = Color.Black,
            labelVisible = true,
            strokeWidth = 70f,
            activeSliceAlpha = .9f,
            isAnimationEnable = true
        )
        Column {
            Legends(
                legendsConfig = DataUtils.getLegendsConfigFromPieChartData(
                    donutChartData,
                    3
                ).copy(
                    textStyle = TextStyle(
                        fontSize = 16.sp, // Размер шрифта
                        fontFamily = FontFamily.SansSerif // Шрифт),
                    )
                )
            )
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                DonutPieChart(
                    modifier = Modifier
                        .height(350.dp),
                    donutChartData,
                    donutChartConfig
                )
                { slice ->
                    Toast.makeText(context, slice.label, Toast.LENGTH_SHORT).show()
                }
            }
        }


    }

    private fun getRandomColor(): Color {
        val rnd = Random.Default
        return Color(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255))
    }
}