package com.example.myfinapp.ui.home

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfinapp.Repository
import com.example.myfinapp.room.CardEntity
import com.example.myfinapp.room.CategoryEntity
import com.example.myfinapp.room.MonthlyCategorySummaryEntity
import com.example.myfinapp.room.OperationEntity
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.launch

class HomeViewModel(val repository: Repository) : ViewModel() {

    val _mcsList = MutableLiveData<List<MonthlyCategorySummaryEntity>>()
    fun insertCard(cardNumber: String) {
        val card = CardEntity(
            cardNumber = cardNumber,
            bankName = "Сбербанк"
        )
        viewModelScope.launch {
            repository.insertCard(card)
        }
    }

    fun insertCategory(categoryName: String) {
        viewModelScope.launch {
            val category = CategoryEntity(
                categoryName = categoryName
            )
            repository.insertCategory(category)
        }
    }

    fun insertMcs(sum: Double, date: String, categoryId: Long) {
        viewModelScope.launch {
            val mcs = MonthlyCategorySummaryEntity(
                plus = 0.0,
                minus = sum,
                date = date,
                categoryId = categoryId
            )
            repository.insertMcs(mcs)
        }
    }

    fun getAllMcs() {
        viewModelScope.launch {
            _mcsList.value = repository.getAllMcs()
        }
    }

    fun insertOperation(
        sum: Double,
        date: String,
        income: Boolean,
        cardId: Long,
        categoryId: Long
    ) {
        viewModelScope.launch {
            val operation = OperationEntity(
                sum = sum,
                date = date,
                income = income,
                cardId = cardId,
                categoryId = categoryId
            )
            repository.insertOperation(operation)
        }
    }

    fun parseData2(text: String) {
        val lines = text.split("\n")
        Log.d("lines count", lines.count().toString())
        for ((i, line) in lines.withIndex()) {
            Log.d("line$i", line)
        }
        val operations = mutableListOf<Operation>()

        for (i in 0 until lines.count() - 1) {

            val dateTimeCategory = lines[i]
            val dateTimeCardOperation = lines[i + 1]

            val dateTimeRegex = Regex("""(\d{2}\.\d{2}\.\d{4}) (\d{2}:\d{2})""")
            val categoryRegex =
                Regex("""(\d{2}\.\d{2}\.\d{4}) (\d{2}:\d{2}) (.*) (\d+,\d{2})""")
            val cardRegex =
                Regex("""(\d{2}\.\d{2}\.\d{4}) (\d+) (.*) Операция по карте \*\*\*\*(\d{4})""")

            val dateTimeMatch = dateTimeRegex.find(dateTimeCategory)
            val categoryMatch = categoryRegex.find(dateTimeCategory)
            val cardMatch = cardRegex.find(dateTimeCardOperation)

            if (dateTimeMatch != null && categoryMatch != null && cardMatch != null) {
                val date = dateTimeMatch.groupValues[1]
                val time = dateTimeMatch.groupValues[2]
                val category = categoryMatch.groupValues[3]
                val amount = categoryMatch.groupValues[4]
                val operationName = cardMatch.groupValues[3]
                val card = cardMatch.groupValues[4]

                val newOperation = Operation(
                    date = date,
                    time = time,
                    category = category,
                    amount = if (amount.startsWith("+")) {
                        amount.drop(1).replace(",", ".").toDouble()
                    } else {
                        amount.replace(",", ".").toDouble()
                    },
                    income = false,
                    description = operationName,
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
            Log.d("amount", operation.amount.toString())
            Log.d("operationName", operation.description)
            Log.d("card", operation.card)
        }
    }

    fun parseData(text: String): String {
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

    fun readTextFromUri(uri: Uri, context: Context): String {
        val stringBuilder = StringBuilder()
        PDFBoxResourceLoader.init(context)
        val contentResolver = context.contentResolver
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
        val amount: Double,
        val income: Boolean,
        val description: String,
        val card: String
    )
}
