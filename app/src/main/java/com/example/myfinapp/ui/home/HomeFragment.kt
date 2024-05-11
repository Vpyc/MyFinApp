package com.example.myfinapp.ui.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var db: MyFinDb
    private lateinit var cardDao: CardDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var operationDao: OperationDao
    private lateinit var mcsDao: MCSDao
    private lateinit var contract: ActivityResultLauncher<Intent>
    private lateinit var intent: Intent

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
        binding.button.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                cardDao.insertCard(
                    CardEntity(
                        cardNumber = binding.cardNumb.text.toString(),
                    )
                )
            }
        }
        binding.button2.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                categoryDao.insertCategory(
                    CategoryEntity(
                        categoryName = binding.categoryName.text.toString(),
                    )
                )

            }
        }
        binding.button3.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                mcsDao.insertMcs(
                    MonthlyCategorySummaryEntity(
                        plus = 0.toDouble(),
                        minus = binding.sum.text.toString().toDouble(),
                        date = binding.editTextTime.text.toString(),
                        categoryId = binding.categoryId.text.toString().toLong(),
                    )
                )
                operationDao.insertOperation(
                    OperationEntity(
                        sum = binding.sum.text.toString().toDouble(),
                        date = binding.editTextDate.text.toString(),
                        income = false,
                        cardId = binding.cardId.text.toString().toLong(),
                        categoryId = binding.categoryId.text.toString().toLong(),
                    )
                )

            }
        }
        binding.button4.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val str = mcsDao.getAllMCS()
                Log.d("str", str.toString())
                Log.d("Date", str[0].date)

            }
        }
        binding.button5.setOnClickListener {
            selectPDFFile()
        }

    }

    private fun selectPDFFile() {
        contract.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handlePDFFileSelection(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.also { uri ->
                CoroutineScope(Dispatchers.IO).launch {
                    val text = parseData(readTextFromUri(uri))
                    parseData2(text)
                }
            }
        }
    }

    private fun parseData2(text: String) {
        val lines = text.split("\n")
        Log.d("lines count", lines.count().toString())
        for((i, line) in lines.withIndex()) {
            Log.d("line$i", line)
        }
        val operations = mutableListOf<Operation>()

        for (i in 0 until lines.count()-1) {

            val dateTimeCategory = lines[i]
            val dateTimeCardOperation = lines[i + 1]

            val dateTimeRegex = Regex("""(\d{2}\.\d{2}\.\d{4}) (\d{2}:\d{2})""")
            val categoryRegex = Regex("""(\d{2}\.\d{2}\.\d{4}) (\d{2}:\d{2}) (.*) (\d+,\d{2})""")
            val cardRegex = Regex("""(\d{2}\.\d{2}\.\d{4}) (\d+) (.*) Операция по карте \*\*\*\*(\d{4})""")

            val dateTimeMatch = dateTimeRegex.find(dateTimeCategory)
            val categoryMatch = categoryRegex.find(dateTimeCategory)
            val cardMatch = cardRegex.find(dateTimeCardOperation)

            if (dateTimeMatch != null && categoryMatch != null && cardMatch != null) {
                val date = dateTimeMatch.groupValues[1]
                val time = dateTimeMatch.groupValues[2]
                val category = categoryMatch.groupValues[3]
                val amount = categoryMatch.groupValues[4]
                val code = cardMatch.groupValues[2]
                val operationName = cardMatch.groupValues[3]
                val card = cardMatch.groupValues[4]

                val newOperation = Operation(
                    date = date,
                    time = time,
                    category = category,
                    amount = amount,
                    code = code,
                    operationName = operationName,
                    card = card
                )
                operations.add(newOperation)
            }
        }

        Log.d("operations count", operations.count().toString())
        for (operation in operations) {
            Log.d("date", operation.date)
            Log.d("time", operation.time)
            Log.d("category", operation.category)
            Log.d("amount", operation.amount)
            Log.d("code", operation.code)
            Log.d("operationName", operation.operationName)
            Log.d("card", operation.card)
        }
    }
    private fun parseData(text: String): String {
        val dateRegex =
            Regex("""^(\d{2}\.\d{2}\.\d{4})""") // Регулярное выражение для даты в формате "дд.мм.гггг"
        val lines = text.split("\n") // Разделение текста на строки

        val relevantData = StringBuilder()

        for (line in lines) {
            var relevantDataFound = false
            if (dateRegex.find(line) != null) {
                relevantDataFound = true
            }

            if (relevantDataFound) {
                relevantData.append(line).append("\n")
            }
        }
        val relevantDataText = relevantData.toString()
        Log.d("relText", relevantDataText)
        return relevantDataText
    }

    private fun readTextFromUri(uri: Uri): String {
        val stringBuilder = StringBuilder()
        PDFBoxResourceLoader.init(requireContext())
        val contentResolver = requireContext().contentResolver
        contentResolver.openInputStream(uri)?.use { inputStream ->
            val document = PDDocument.load(inputStream)
            val stripper = PDFTextStripper()
            stripper.startPage = 1
            stripper.endPage = document.numberOfPages
            stripper.sortByPosition = true
            val text = stripper.getText(document)
            stringBuilder.append(text)
            document.close()
        }
        Log.d("text", stringBuilder.toString())
        return stringBuilder.toString()
    }

    data class Operation(
        val date: String,
        val time: String,
        val category: String,
        val amount: String,
        val code: String,
        val operationName: String,
        val card: String
    )

}

