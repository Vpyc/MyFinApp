package com.example.myfinapp.ui.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinapp.databinding.FragmentHomeBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var contractPdf: ActivityResultLauncher<Intent>
    private lateinit var intent: Intent
    private lateinit var homeViewModel: HomeViewModel
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
        val recyclerView = binding.recyclerView
        val topMenu = binding.topMenu
        var isMenuVisible = true
        var scrollDistance = 0

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                scrollDistance += dy
                if (scrollDistance > 50 && isMenuVisible) {
                    isMenuVisible = false
                    topMenu.animate().translationY(-topMenu.height.toFloat())
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                super.onAnimationEnd(animation)
                                topMenu.visibility = View.GONE
                            }
                        }).start()
                } else if (scrollDistance < -50 && !isMenuVisible) {
                    isMenuVisible = true
                    topMenu.animate().translationY(0f)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationStart(animation: Animator) {
                                super.onAnimationStart(animation)
                                topMenu.visibility = View.VISIBLE
                            }
                        }).start()
                }
                if (scrollDistance < -50) scrollDistance = 0
                if (scrollDistance > 50) scrollDistance = 0
            }
        })
        val groupAdapter = GroupAdapter<GroupieViewHolder>()
        lifecycleScope.launch {
            homeViewModel.operationsList.collect { operationGroups ->
                Log.d("HomeFragment", "OperationGroups: $operationGroups")
                groupAdapter.clear()
                val categoryNames = "Все"
                val cardNames = "Все"
                val income = null
                val filteredOperationGroups = filterOperationGroups(
                    operationGroups,
                    categoryNames,
                    cardNames,
                    income)

                for (operationGroup in filteredOperationGroups) {
                    val operationGroupItem = OperationGroupItem(operationGroup)
                    groupAdapter.add(operationGroupItem)

                    for (operation in operationGroup.operations) {
                        val operationViewItem = OperationViewItem(operation)
                        groupAdapter.add(operationViewItem)
                    }
                }
                recyclerView.adapter = groupAdapter
            }
        }
        lifecycleScope.launch {
            homeViewModel.selectedIncome.collect { income ->
                Log.d("HomeFragment", "Income: $income")
                val operationGroups = homeViewModel.operationsList.value
                groupAdapter.clear()
                val categoryNames = homeViewModel.selectedCategory.value
                val cardNames = homeViewModel.selectedCard.value
                val filteredOperationGroups = filterOperationGroups(
                    operationGroups,
                    categoryNames,
                    cardNames,
                    income
                )
                for (operationGroup in filteredOperationGroups) {
                    val operationGroupItem = OperationGroupItem(operationGroup)
                    groupAdapter.add(operationGroupItem)

                    for (operation in operationGroup.operations) {
                        val operationViewItem = OperationViewItem(operation)
                        groupAdapter.add(operationViewItem)
                    }
                }
                recyclerView.adapter = groupAdapter
            }
        }
        lifecycleScope.launch {
            homeViewModel.selectedCategory.collect { categoryNames ->
                Log.d("HomeFragment", "CategoryNames: $categoryNames")
                val operationGroups = homeViewModel.operationsList.value
                groupAdapter.clear()
                val income = homeViewModel.selectedIncome.value
                val cardNames = homeViewModel.selectedCard.value
                val filteredOperationGroups = filterOperationGroups(
                    operationGroups,
                    categoryNames,
                    cardNames,
                    income
                )
                for (operationGroup in filteredOperationGroups) {
                    val operationGroupItem = OperationGroupItem(operationGroup)
                    groupAdapter.add(operationGroupItem)

                    for (operation in operationGroup.operations) {
                        val operationViewItem = OperationViewItem(operation)
                        groupAdapter.add(operationViewItem)
                    }
                }
                recyclerView.adapter = groupAdapter
            }
        }
        lifecycleScope.launch {
            homeViewModel.selectedCard.collect { cardNames ->
                Log.d("HomeFragment", "CardNames: $cardNames")
                val operationGroups = homeViewModel.operationsList.value
                groupAdapter.clear()
                val income = homeViewModel.selectedIncome.value
                val categoryNames = homeViewModel.selectedCategory.value
                val filteredOperationGroups = filterOperationGroups(
                    operationGroups,
                    categoryNames,
                    cardNames,
                    income
                )
                for (operationGroup in filteredOperationGroups) {
                    val operationGroupItem = OperationGroupItem(operationGroup)
                    groupAdapter.add(operationGroupItem)

                    for (operation in operationGroup.operations) {
                        val operationViewItem = OperationViewItem(operation)
                        groupAdapter.add(operationViewItem)
                    }
                }
                recyclerView.adapter = groupAdapter
            }
        }
        binding.addOperationButton.setOnClickListener {
            showOperationFragment(false)
        }

        binding.uploadPdf.setOnClickListener {
            contractPdf.launch(intent)
        }

        binding.filter.setOnClickListener {
            val filterFragment = FilterFragment()
            filterFragment.show(parentFragmentManager, "FilterFragment")
        }
    }
    private fun filterOperationGroups(
        operationGroups: List<OperationGroup>,
        categoryName: String,
        cardName: String,
        income: Boolean?
    ): List<OperationGroup> {
        return operationGroups.map { group ->
            val filteredOperations = group.operations.filter { operation ->
                (categoryName == "Все" || operation.categoryName in categoryName) &&
                        (cardName == "Все" || operation.cardNumber in cardName) &&
                        (income == null || operation.income == income)
            }
            OperationGroup(group.date, filteredOperations)
        }.filter { group -> group.operations.isNotEmpty() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showOperationFragment(visible: Boolean = true) {
        val operationFragment = OperationFragment()
        val args = Bundle()
        args.putBoolean("isButtonVisible", visible) // Установите значение видимости кнопки
        operationFragment.arguments = args
        operationFragment.show(parentFragmentManager, "OperationFragment")
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

