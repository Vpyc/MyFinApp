package com.example.myfinapp.ui.cushion

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
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
import com.example.myfinapp.databinding.FragmentFinancialCushionBinding

class FinancialCushionFragment : Fragment() {
    private var _binding: FragmentFinancialCushionBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinancialCushionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val composeView = binding.donutChart
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
                    Surface(
                        color = MaterialTheme.colorScheme.background, // Используем цвет фона из текущей темы
                    ) {
                        DonutChart()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Preview
    @Composable
    fun DonutChart() {
        val donutChartData = PieChartData(
            slices = listOf(
                PieChartData.Slice(
                    value = 7818.58f,
                    color = colorResource(id = R.color.green),
                    label = "Остаток"
                ),
                PieChartData.Slice(
                    value = 382.99f,
                    color = colorResource(id = R.color.red),
                    label = "Расходы за месяц"
                ),
            ), plotType = PlotType.Donut
        )

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
                    2
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
            }
        }
    }
}