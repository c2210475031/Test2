package com.example.financetracker.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.financetracker.database.model.Category
import com.example.financetracker.database.model.Transaction
import com.example.financetracker.database.repository.TransactionRepository
import com.example.financetracker.model.TransactionF
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class GlobalViewModelFactory(
    private val repository: TransactionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GlobalViewModel(repository) as T
    }
}

class GlobalViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _allTransactionsFlow: StateFlow<List<Transaction>> =
        repository.allTransactions.asFlow()
            .map { it } // No transformation needed here yet
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )


    private val allTransactionsHardCoded = listOf(
        TransactionF(1000.0, "2025-05-25", "Salary", true),
        TransactionF(50.0, "2025-05-24", "Groceries", false),
        TransactionF(15.5, "2025-05-23", "Coffee", false)
    )

    val allCategories: LiveData<List<Category>> = repository.allCategories
    val allTransactions: LiveData<List<Transaction>> = repository.allTransactions

    fun insertCategory(category: Category) {
        viewModelScope.launch {
            try {
                repository.insertCategory(category)
                Log.i("TransactionViewModel", "Inserting Category succeeded")
            } catch (e: Exception) {
                Log.e("TransactionViewModel", "Inserting Category failed", e)
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            try {
                repository.deleteCategory(category)
                Log.i("TransactionViewModel", "Deleting Category succeeded")
            } catch (e: Exception) {
                Log.e("TransactionViewModel", "Deleting Category failed", e)
            }
        }
    }

    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                repository.insertTransaction(transaction)
                Log.i("TransactionViewModel", "Inserting of Transaction succeeded")
            } catch (e: Exception) {
                Log.e("TransactionViewModel", "Inserting of Transaction failed", e)
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                repository.deleteTransaction(transaction)
                Log.i("TransactionViewModel", "Deleting Transaction succeeded")
            } catch (e: Exception) {
                Log.e("TransactionViewModel", "Deleting Transaction failed", e)
            }
        }
    }

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
    var transactions: StateFlow<List<Transaction>> =
        combine(_allTransactionsFlow, _filter, _startDate, _endDate) { all, type, start, end ->
            var filtered = all

            filtered = when (type) {
                "Income" -> filtered.filter { it.isPositive }
                "Expenses" -> filtered.filter { !it.isPositive }
                else -> filtered
            }

            if (start != null && end != null) {
                filtered = filtered.filter {
                    val date = it.timestamp.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                    !date.isBefore(start) && !date.isAfter(end)
                }
            }

            filtered
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )





    /*@RequiresApi(Build.VERSION_CODES.O)
    val transactions: StateFlow<List<TransactionF>> =
        combine(_filter, _startDate, _endDate) { type, start, end ->
            var filtered = allTransactionsHardCoded

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
            initialValue = allTransactionsHardCoded
        )*/
}

