package com.example.myfinapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myfinapp.databinding.FragmentHomeBinding
import com.example.myfinapp.room.CardDao
import com.example.myfinapp.room.CardEntity
import com.example.myfinapp.room.CategoryDao
import com.example.myfinapp.room.CategoryEntity
import com.example.myfinapp.room.MCSDao
import com.example.myfinapp.room.MonthlyCategorySummaryEntity
import com.example.myfinapp.room.MyFinDb
import com.example.myfinapp.room.OperationDao
import com.example.myfinapp.room.OperationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var db: MyFinDb
    private lateinit var cardDao: CardDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var operationDao: OperationDao
    private lateinit var mcsDao: MCSDao
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        db = MyFinDb.getDb(requireContext())
        cardDao = db.getCardDao()
        categoryDao = db.getCategoryDao()
        operationDao = db.getOperationDao()
        mcsDao = db.getMCSDao()

        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.button.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                cardDao.insertCard(CardEntity(
                    cardNumber = binding.cardNumb.text.toString(),
                ))

            }
        }
        binding.button2.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                categoryDao.insertCategory(CategoryEntity(
                    categoryName = binding.categoryName.text.toString(),
                ))

            }
        }
        binding.button3.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                mcsDao.insertMcs(MonthlyCategorySummaryEntity(
                    plus = 0.toDouble(),
                    minus = binding.sum.text.toString().toDouble(),
                    date = binding.editTextTime.text.toString(),
                    categoryId = binding.categoryId.text.toString().toLong(),
                ))
                operationDao.insertOperation(OperationEntity(
                    sum = binding.sum.text.toString().toDouble(),
                    date = binding.editTextDate.text.toString(),
                    income = false,
                    cardId = binding.cardId.text.toString().toLong(),
                    categoryId = binding.categoryId.text.toString().toLong(),
                    ))

            }
        }
        binding.button4.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val str = mcsDao.getAllMCS()
                Log.d("str", str.toString())
                Log.d("Date", str[0].date)

            }
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}