package com.example.myfinapp.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfinapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var contractPdf: ActivityResultLauncher<Intent>
    private lateinit var intent: Intent
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var adapter: OperationsAdapter
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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = OperationsAdapter()
        binding.recyclerView.adapter = adapter
        intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "application/pdf"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        contractPdf = registerForActivityResult(StartActivityForResult()) { result ->
            handlePDFFileSelection(result)
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            homeViewModel.operationsList.collect { operations ->
                adapter.submitList(operations)
            }
        }

        binding.button5.setOnClickListener {
            contractPdf.launch(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handlePDFFileSelection(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.also { uri ->
                CoroutineScope(Dispatchers.IO).launch {
                    val text = homeViewModel.parseData(
                        homeViewModel.readTextFromUri(
                            uri,
                            requireContext()
                        )
                    )
                    homeViewModel.parseData2(text)
                }
            }
        }
    }
}

