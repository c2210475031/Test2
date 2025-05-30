package com.example.financetracker.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.financetracker.database.model.Category
import com.example.financetracker.database.model.Transaction
import com.example.financetracker.database.model.User
import com.example.financetracker.database.repository.TransactionRepository
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
        repository.allTransactions.asFlow().map { it } // No transformation needed here yet
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val allUsers: LiveData<List<User>> = repository.allUsers
    val allTransactions: LiveData<List<Transaction>> = repository.allTransactions
    val allCategories: LiveData<List<Category>> = repository.allCategories

    fun insertUser(user: User) {
        viewModelScope.launch {
            try {
                repository.insertUser(user)
                Log.i("GlobalViewModel", "Insert of User succeeded")
            } catch (e: Exception) {
                Log.e("GlobalViewModel", "Insert of User failed", e)
            }
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            try {
                repository.updateUser(user)
                Log.i("GlobalViewModel", "Update of User succeeded")
            } catch (e: Exception) {
                Log.e("GlobalViewModel", "Update of User failed", e)
            }
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            try {
                repository.deleteUser(user)
                Log.i("GlobalViewModel", "Delete of User succeeded")
            } catch (e: Exception) {
                Log.e("GlobalViewModel", "Delete of User failed", e)
            }
        }
    }

    fun insertCategory(category: Category) {
        viewModelScope.launch {
            try {
                repository.insertCategory(category)
                Log.i("GlobalViewModel", "Inserting Category succeeded")
            } catch (e: Exception) {
                Log.e("GlobalViewModel", "Inserting Category failed", e)
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            try {
                repository.deleteCategory(category)
                Log.i("GlobalViewModel", "Deleting Category succeeded")
            } catch (e: Exception) {
                Log.e("GlobalViewModel", "Deleting Category failed", e)
            }
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            try {
                repository.updateCategory(category)
                Log.i("GlobalViewModel", "Updating Category succeeded")
            } catch (e: Exception) {
                Log.e("GlobalViewModel", "Updating Category failed", e)
            }
        }
    }

    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                repository.insertTransaction(transaction)
                Log.i("GlobalViewModel", "Inserting of Transaction succeeded")
            } catch (e: Exception) {
                Log.e("GlobalViewModel", "Inserting of Transaction failed", e)
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                repository.deleteTransaction(transaction)
                Log.i("GlobalViewModel", "Deleting Transaction succeeded")
            } catch (e: Exception) {
                Log.e("GlobalViewModel", "Deleting Transaction failed", e)
            }
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                repository.updateTransaction(transaction)
                Log.i("GlobalViewModel", "Updating Transaction succeeded")
            } catch (e: Exception) {
                Log.e("GlobalViewModel", "Updating Transaction failed", e)
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
}
