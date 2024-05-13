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
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class HomeViewModel(private val repository: Repository) : ViewModel() {

    val _mcsList = MutableLiveData<List<MonthlyCategorySummaryEntity>>()
    suspend fun insertCard(cardNumber: String): Long {
        return suspendCoroutine { continuation ->
            viewModelScope.launch {
                val card = CardEntity(
                    cardNumber = cardNumber,
                )
                continuation.resume(repository.insertCard(card))
            }
        }
    }
    suspend fun insertCategory(categoryName: String): Long {
        return suspendCoroutine { continuation ->
            viewModelScope.launch {
                val category = CategoryEntity(
                    categoryName = categoryName
                )
                continuation.resume(repository.insertCategory(category))
            }
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
        sum: String,
        date: String,
        income: Boolean,
        description: String,
        cardId: Long,
        categoryId: Long
    ) {
        viewModelScope.launch {
            val operation = OperationEntity(
                sum = sum.toDouble(),
                date = date,
                income = income,
                cardId = cardId,
                categoryId = categoryId
            )
            repository.insertOperation(operation)
        }
    }
    private suspend fun findCardId(cardNumber: String): Long {
        return suspendCoroutine { continuation ->
            viewModelScope.launch {
                var cardId = repository.findCardByCardNumber(cardNumber)
                if (cardId == null) {
                    cardId = insertCard(cardNumber)
                }
                continuation.resume(cardId ?: -1L)
            }
        }
    }
    private suspend fun findCategoryId(categoryName: String): Long {
        return suspendCoroutine { continuation ->
            viewModelScope.launch {
                var categoryId = repository.findCategoryByCategoryName(categoryName)
                if (categoryId == null) {
                    categoryId = insertCategory(categoryName)
                }
                continuation.resume(categoryId ?: -1L)
            }
        }
    }

    fun parseData2(text: String) {
        val lines = text.split("\n")
        Log.d("lines count", lines.count().toString())
        for ((i, line) in lines.withIndex()) {
            Log.d("line$i", line)
        }
        val cardNumber = findCardNumber(lines)
        viewModelScope.launch {
            val cardId = findCardId(cardNumber)
            for (i in 0 until lines.count() - 1 step 2) {
                val dateTimeCategory = lines[i]
                val income = findPlus(dateTimeCategory)
                val dateTimeCardOperation = lines[i + 1]
                val dateTimeRegex = Regex("""(\d{2}\.\d{2}\.\d{4} \d{2}:\d{2})""")
                val categoryRegex =
                    Regex("""(\d{2}\.\d{2}\.\d{4}) (\d{2}:\d{2}) (.*) ([+]?[\d\s]+,\d{2})""")
                val cardRegex =
                    Regex("""(\d{2}\.\d{2}\.\d{4}) (\d+) (.*) Операция по карте \*\*\*\*(\d{4})""")

                val dateTimeMatch = dateTimeRegex.find(dateTimeCategory)
                val categoryMatch = categoryRegex.find(dateTimeCategory)
                val cardMatch = cardRegex.find(dateTimeCardOperation)

                if (dateTimeMatch != null && categoryMatch != null && cardMatch != null) {
                    val date = dateTimeMatch.groupValues[1]
                    val category = categoryMatch.groupValues[3]
                    val categoryId = findCategoryId(category)
                    val description = cardMatch.groupValues[3]
                    val sum = categoryMatch.groupValues[4]
                        .replace(",", ".")
                        .replace("\u00A0", "")
                    if (income) {
                        sum.drop(1)
                    }
                    insertOperation(sum, date, income, description, cardId, categoryId)
                }
            }
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
    private fun findPlus(line: String): Boolean {
        return line.contains("+")
    }

    private fun findCardNumber(lines: List<String>): String {
        val cardRegex =
            Regex("""(\d{2}\.\d{2}\.\d{4}) (\d+) (.*) Операция по карте \*\*\*\*(\d{4})""")
        for (i in 0 until lines.count() - 1 step 2) {
            val dateTimeCardOperation = lines[i + 1]
            val cardMatch = cardRegex.find(dateTimeCardOperation)
            if (cardMatch!= null) {
                return cardMatch.groupValues[4]
            }
        }
        return "" // Если не нашли номер карты, то возвращаем пустую строку
    }
}
