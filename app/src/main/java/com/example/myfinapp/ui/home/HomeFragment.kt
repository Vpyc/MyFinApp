package com.example.myfinapp.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myfinapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var contract: ActivityResultLauncher<Intent>
    private lateinit var intent: Intent
    private lateinit var homeViewModel: HomeViewModel
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this, HomeViewModelFactory(requireContext()))
            .get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "application/pdf"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        contract = registerForActivityResult(StartActivityForResult()) { result ->
            handlePDFFileSelection(result)
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel._mcsList.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_LONG).show()
        }
        binding.button.setOnClickListener {
            homeViewModel.insertCard(binding.cardId.text.toString())
        }
        binding.button2.setOnClickListener {
            homeViewModel.insertCategory(
                binding.categoryName.text.toString())
        }
        binding.button3.setOnClickListener {
            homeViewModel.insertMcs(
                binding.sum.text.toString().toDouble(),
                binding.editTextTime.text.toString(),
                binding.categoryId.text.toString().toLong()
            )
            homeViewModel.insertOperation(
                binding.sum.text.toString().toDouble(),
                binding.editTextDate.text.toString(),
                false,
                binding.cardId.text.toString().toLong(),
                binding.categoryId.text.toString().toLong(),
            )
        }
        binding.button4.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                homeViewModel.getAllMcs()
            }
        }
        binding.button5.setOnClickListener {
            contract.launch(intent)
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
                    val text = homeViewModel.parseData(homeViewModel.readTextFromUri(uri, requireContext()) )
                    homeViewModel.parseData2(text)
                }
            }
        }
    }
}

