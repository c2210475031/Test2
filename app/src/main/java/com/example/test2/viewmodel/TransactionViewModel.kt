package com.example.test2.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.test2.model.TransactionF
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class TransactionViewModel : ViewModel() {

    private val allTransactions = listOf(
        TransactionF(1000.0, "2025-05-25", "Salary", true),
        TransactionF(50.0, "2025-05-24", "Groceries", false),
        TransactionF(15.5, "2025-05-23", "Coffee", false)
    )

    private val _filter = MutableStateFlow("All")
    val filter: StateFlow<String> = _filter.asStateFlow()

    private val _startDate = MutableStateFlow<LocalDate?>(null)
    val startDate: StateFlow<LocalDate?> = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow<LocalDate?>(null)
    val endDate: StateFlow<LocalDate?> = _endDate.asStateFlow()

    fun setStartDate(date: LocalDate?) {
        _startDate.value = date
    }

    fun setEndDate(date: LocalDate?) {
        _endDate.value = date
    }


    fun setFilter(newFilter: String) {
        _filter.value = newFilter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val transactions: StateFlow<List<TransactionF>> = combine(_filter, _startDate, _endDate) { type, start, end ->
        var filtered = allTransactions

        // Filter by type
        filtered = when (type) {
            "Income" -> filtered.filter { it.isPositive }
            "Expenses" -> filtered.filter { !it.isPositive }
            else -> filtered
        }

        // Filter by date range if both start and end are set
        if (start != null && end != null) {
            filtered = filtered.filter {
                val date = LocalDate.parse(it.date, DateTimeFormatter.ISO_DATE)
                !date.isBefore(start) && !date.isAfter(end)
            }
        }

        filtered
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = allTransactions
    )
}

