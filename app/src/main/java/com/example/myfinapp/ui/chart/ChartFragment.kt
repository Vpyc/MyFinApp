package com.example.myfinapp.ui.chart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myfinapp.databinding.FragmentChartBinding
import com.example.myfinapp.room.McsItem

class ChartFragment: Fragment(){
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
        Log.d("ChartFragmentInfo", mcs.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

}