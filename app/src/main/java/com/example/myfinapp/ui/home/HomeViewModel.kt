package com.example.myfinapp.ui.home

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfinapp.Repository
import com.example.myfinapp.room.CardEntity
import com.example.myfinapp.room.CategoryEntity
import com.example.myfinapp.room.MonthlyCategorySummaryEntity
import com.example.myfinapp.room.OperationEntity
import com.example.myfinapp.room.OperationItem
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class HomeViewModel(private val repository: Repository, private val converter: DateConverter) :
    ViewModel() {
    private val mutex = Mutex()

    private val _operationsList = MutableStateFlow(emptyList<OperationItem>())
    val operationsList = _operationsList.asStateFlow()

    init {
        getOperations()
    }

    private fun getOperations() {
        viewModelScope.launch {
            repository.getAllOperationsWithFormattedData().flowOn(Dispatchers.IO)
                .collect { operations: List<OperationItem> ->
                    _operationsList.update { operations }
                }
        }
    }

    private suspend fun insertCard(cardNumber: String): Long {
        return suspendCoroutine { continuation ->
            viewModelScope.launch(Dispatchers.IO) {
                val card = CardEntity(
                    cardNumber = cardNumber,
                )
                continuation.resume(repository.insertCard(card))
            }
        }
    }

    private suspend fun insertCategory(categoryName: String): Long {
        return suspendCoroutine { continuation ->
            viewModelScope.launch(Dispatchers.IO) {
                val category = CategoryEntity(
                    categoryName = categoryName
                )
                continuation.resume(repository.insertCategory(category))
            }
        }
    }

    private fun insertOperation(
        sum: String,
        date: Long,
        income: Boolean,
        description: String,
        cardId: Long,
        categoryId: Long
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val operation = OperationEntity(
                sum = sum.toDouble(),
                date = date,
                income = income,
                description = description,
                cardId = cardId,
                categoryId = categoryId
            )
            repository.insertOperation(operation)
        }
    }

    private suspend fun findMcsByDateAndCategoryId(
        date: Long,
        categoryId: Long
    ): MonthlyCategorySummaryEntity? {
        return suspendCoroutine { continuation ->
            viewModelScope.launch {
                continuation.resume(repository.findMcsByDateAndCategoryId(date, categoryId))
            }
        }
    }

    private suspend fun updateOrInsertMcs(
        plus: String,
        minus: String,
        date: Long,
        categoryId: Long
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.withTransaction {
                val mcs = findMcsByDateAndCategoryId(date, categoryId)
                if (mcs == null) {
                    val newMcs = MonthlyCategorySummaryEntity(
                        plus = plus.toDouble(),
                        minus = minus.toDouble(),
                        date = date,
                        categoryId = categoryId
                    )
                    repository.insertMcs(newMcs)
                } else {
                    mcs.plus += plus.toDouble()
                    mcs.minus += minus.toDouble()
                    repository.updateMcs(mcs)
                }
            }
        }
    }

    private suspend fun findCardId(cardNumber: String): Long {
        return suspendCoroutine { continuation ->
            viewModelScope.launch {
                var cardId = repository.findCardByCardNumber(cardNumber)
                if (cardId == null) {
                    cardId = insertCard(cardNumber)
                }
                continuation.resume(cardId)
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
                continuation.resume(categoryId)
            }
        }
    }

    private fun insertOperationAndMcsOrSkip(
        sum: String,
        date: Long,
        monthYear: Long,
        income: Boolean,
        description: String,
        cardId: Long,
        categoryId: Long
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.withTransaction {

                val operation =
                    repository.findOperationByAll(
                        sum.toDouble(),
                        date,
                        income,
                        description,
                        cardId,
                        categoryId
                    )
                if (operation == null) {
                    insertOperation(sum, date, income, description, cardId, categoryId)
                    mutex.withLock {
                        if (income) {
                            updateOrInsertMcs(sum, "0", monthYear, categoryId)
                            Log.d(
                                "Update plus",
                                converter.convertMcsFromLong(monthYear) + " " + categoryId
                            )
                        } else {
                            updateOrInsertMcs("0", sum, monthYear, categoryId)
                            Log.d(
                                "Update minus",
                                converter.convertMcsFromLong(monthYear) + " " + categoryId
                            )
                        }
                    }
                }
            }
        }
    }

    fun parseData2(text: String) {
        val lines = text.split("\n")
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
                    Regex("""(\d{2}\.\d{2}\.\d{4}) (\d+) (.*?)(Операция по карте \*\*\*\*(\d{4}))?$""")
                val monthYearRegex = Regex("""(\d{2}\.)(\d{2}\.\d{4}) """)
                val monthYearMatch = monthYearRegex.find(dateTimeCategory)
                val dateTimeMatch = dateTimeRegex.find(dateTimeCategory)
                val categoryMatch = categoryRegex.find(dateTimeCategory)
                val cardMatch = cardRegex.find(dateTimeCardOperation)

                if (dateTimeMatch != null && categoryMatch != null && cardMatch != null && monthYearMatch != null) {
                    val date = dateTimeMatch.groupValues[1]
                    val monthYear = monthYearMatch.groupValues[2]
                    val category = categoryMatch.groupValues[3]
                    val categoryId = findCategoryId(category)
                    val description = cardMatch.groupValues[3]
                    val sum = categoryMatch.groupValues[4]
                        .replace(",", ".")
                        .replace("\u00A0", "")
                    if (income) {
                        sum.drop(1)
                    }
                    insertOperationAndMcsOrSkip(
                        sum,
                        converter.convertOperationToLong(date),
                        converter.convertMcsToLong(monthYear),
                        income,
                        description,
                        cardId,
                        categoryId
                    )
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
        return relevantData.toString()
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
            if (cardMatch != null) {
                return cardMatch.groupValues[4]
            }
        }
        return "" // Если не нашли номер карты, то возвращаем пустую строку
    }

}
