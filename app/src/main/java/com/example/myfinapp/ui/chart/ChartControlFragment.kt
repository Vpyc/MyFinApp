package com.example.myfinapp.ui.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
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
        viewLifecycleOwner.lifecycleScope.launch {
            chartControlViewModel.mcsList.collect { mcs ->
                val chartFragment = ChartFragment()
                val bundle = Bundle()
                bundle.putParcelableArrayList("mcs", ArrayList(mcs))
                chartFragment.arguments = bundle
                childFragmentManager.beginTransaction().replace(R.id.chart_fragment, chartFragment).commit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}