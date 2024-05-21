package com.example.myfinapp.ui.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.fragment.app.Fragment
import com.example.myfinapp.databinding.FragmentChartBinding
import com.example.myfinapp.room.McsItem
/*import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry*/
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.axis.DataCategoryOptions
import co.yml.charts.common.model.Point
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarChartType
import co.yml.charts.ui.barchart.models.BarData
import com.example.myfinapp.R

class ChartFragment : Fragment() {
    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentChartBinding.inflate(inflater, container, false)
//        val view =
        /*binding.composeView.apply {
            // Dispose of the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                // In Compose world
                MaterialTheme {
                    Text("Hello Compose!")
                }
            }
        }*/
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mcs = arguments?.getParcelableArrayList<McsItem>("mcs")
        val composeView = view.findViewById<ComposeView>(R.id.compose_view)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                if (!mcs.isNullOrEmpty()){
                    McsBarChart(mcs)
                }
            }
        }
    }

    @Composable
    fun McsBarChart(mcs: ArrayList<McsItem>) {
        val stepSize = 5
        val barsData = createMcsBarChartData(mcs)/*DataUtils.getBarChartData(
            listSize = 8,
            maxRange = 8,
            barChartType = BarChartType.VERTICAL,
            dataCategoryOptions = DataCategoryOptions()
        )*/
        val xAxisData = AxisData.Builder()
            .axisStepSize(100.dp)
            .steps(barsData.size - 1)
            .bottomPadding(40.dp)
            .axisLabelAngle(20f)
            .labelData {index -> barsData[index].label}
            .axisLineColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelColor(MaterialTheme.colorScheme.tertiary)
            .build()
        val yAxisData = AxisData.Builder()
            .steps(stepSize)
            .labelAndAxisLinePadding(20.dp)
            .axisOffset(20.dp)
            .labelData {index -> (index * (100 /stepSize)).toString()}
            .axisLineColor(MaterialTheme.colorScheme.tertiary)
            .axisLabelColor(MaterialTheme.colorScheme.tertiary)
            .build()
        val barChartData = BarChartData(
            chartData = barsData,
            xAxisData = xAxisData,
            yAxisData = yAxisData,
            backgroundColor = MaterialTheme.colorScheme.surface
        )
        BarChart(
            modifier = Modifier
                .height(250.dp),
            barChartData = barChartData
        )
    }
    fun createMcsBarChartData(mcs: ArrayList<McsItem>): List<BarData> {
        val barDataList = arrayListOf<BarData>()

        for (index in mcs.indices) {
            val item = mcs[index]
            val plusValue = item.plus.toFloat()
            val minusValue = item.minus.toFloat() * -1 // Преобразуем минусы

            val point = Point(
                index.toFloat(),
                plusValue
            )

            val barData = item.formattedDate?.let {
                BarData(
                    point = point,
                    color = if (plusValue >= 0) Color.Green else Color.Red, // Зеленый для плюсов, красный для минусов
                    dataCategoryOptions = DataCategoryOptions(),
                    label = it
                )
            }

            if (barData != null) {
                barDataList.add(barData)
            }
        }

        return barDataList
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

}