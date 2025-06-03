@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.financetracker.viewmodel

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.*
import com.example.financetracker.database.model.Category
import com.example.financetracker.database.model.Transaction
import com.example.financetracker.database.model.User
import com.example.financetracker.database.repository.TransactionRepository
import com.example.financetracker.datastore.UserPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
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

    private val _activeUserId = MutableStateFlow<Int?>(null)
    val activeUserId: StateFlow<Int?> = _activeUserId.asStateFlow()

    fun setActiveUser(context: Context, userId: Int) {
        _activeUserId.value = userId

        viewModelScope.launch {
            try {
                UserPreferences.saveUserId(context, userId)
                Log.i("GlobalViewModel", "Active user: ${_activeUserId.value}")
            } catch (e: Exception) {
                Log.e("GlobalViewModel", "Error setting active user", e)
            }
        }

    }

    private val _selectedCategoryId = MutableStateFlow<Int?>(null)
    val selectedCategoryId: StateFlow<Int?> = _selectedCategoryId.asStateFlow()

    fun setSelectedCategoryId(id: Int?) {
        _selectedCategoryId.value = id
    }



    val allUsers: LiveData<List<User>> = repository.allUsers
    val userTransactions: LiveData<List<Transaction>> = activeUserId.flatMapLatest { userId ->
        userId?.let { repository.getAllTransactionsOfUser(it).asFlow() } ?: flowOf(emptyList())
    }.asLiveData()

    val userCategories: LiveData<List<Category>> = activeUserId.flatMapLatest { userId ->
        userId?.let { repository.getAllCategoriesOfUser(it).asFlow() } ?: flowOf(emptyList())
    }.asLiveData()

    suspend fun getAllUsersOnce(): List<User> {
        return repository.getAllUsersOnce()
    }

    suspend fun insertUserAndReturnId(user: User): Int {
        return repository.insertUser(user)
    }

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
    val filteredTransactions: StateFlow<List<Transaction>> =
        activeUserId
            .flatMapLatest { userId ->
                userId?.let { repository.getAllTransactionsOfUser(it).asFlow() }
                    ?: flowOf(emptyList())
            }
            .combine(_filter) { all, type ->
                when (type) {
                    "Budget" -> all.filter { it.isPositive }
                    "Cost" -> all.filter { !it.isPositive }
                    else -> all
                }
            }
            .combine(_startDate) { filtered, startDate ->
                startDate?.let {
                    filtered.filter { tx ->
                        val date =
                            tx.timestamp.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                        !date.isBefore(startDate)
                    }
                } ?: filtered
            }
            .combine(_endDate) { filtered, endDate ->
                endDate?.let {
                    filtered.filter { tx ->
                        val date =
                            tx.timestamp.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                        !date.isAfter(endDate)
                    }
                } ?: filtered
            }
            .combine(_selectedCategoryId) { filtered, categoryId ->
                categoryId?.let { id ->
                    filtered.filter { it.categoryId == id }
                } ?: filtered
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}

