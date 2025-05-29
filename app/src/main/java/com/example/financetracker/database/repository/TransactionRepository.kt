package com.example.financetracker.database.repository

import androidx.lifecycle.LiveData
import com.example.financetracker.database.dao.CategoryDao
import com.example.financetracker.database.dao.TransactionDao
import com.example.financetracker.database.model.Category
import com.example.financetracker.database.model.Transaction

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
) {
    val allTransactions: LiveData<List<Transaction>> = transactionDao.getAllTransactions()
    val allCategories: LiveData<List<Category>> = categoryDao.getAllCategories()

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insert(transaction)
    }

    suspend fun insertCategory(category: Category) {
        categoryDao.insert(category)
    }
}