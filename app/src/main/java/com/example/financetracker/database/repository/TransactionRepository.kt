package com.example.financetracker.database.repository

import androidx.lifecycle.LiveData
import com.example.financetracker.database.dao.CategoryDao
import com.example.financetracker.database.dao.TransactionDao
import com.example.financetracker.database.dao.TransactionTemplateDao
import com.example.financetracker.database.dao.UserDao
import com.example.financetracker.database.model.Category
import com.example.financetracker.database.model.Transaction
import com.example.financetracker.database.model.TransactionTemplate
import com.example.financetracker.database.model.User

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val userDao: UserDao,
    private val templateDao: TransactionTemplateDao
) {
    val allUsers: LiveData<List<User>> = userDao.getAllUsers()
    val allTransactions: LiveData<List<Transaction>> = transactionDao.getAllTransactions()
    val allCategories: LiveData<List<Category>> = categoryDao.getAllCategories()
    val allTemplates: LiveData<List<TransactionTemplate>> = templateDao.getAllTemplates()

    suspend fun getAllUsersOnce(): List<User> {
        return userDao.getAllUsersOnce()
    }

    suspend fun insertUser(user: User):Int = userDao.insertUser(user).toInt()
    suspend fun deleteUser(user: User) = userDao.deleteUser(user)
    suspend fun updateUser(user: User) = userDao.updateUser(user)

    suspend fun insertTransaction(transaction: Transaction) = transactionDao.insert(transaction)
    suspend fun deleteTransaction(transaction: Transaction) = transactionDao.delete(transaction)
    suspend fun updateTransaction(transaction: Transaction) = transactionDao.update(transaction)

    suspend fun insertCategory(category: Category) = categoryDao.insert(category)
    suspend fun deleteCategory(category: Category) = categoryDao.delete(category)
    suspend fun updateCategory(category: Category) = categoryDao.update(category)

    suspend fun insertTemplate(template: TransactionTemplate) = templateDao.insert(template)
    suspend fun deleteTemplate(template: TransactionTemplate) = templateDao.delete(template)
    suspend fun updateTemplate(template: TransactionTemplate) = templateDao.update(template)

    fun getAllTransactionsOfUser(userId: Int): LiveData<List<Transaction>> {
        return transactionDao.getTransactionsOfUser(userId)
    }

    fun getAllCategoriesOfUser(userId: Int): LiveData<List<Category>> {
        return categoryDao.getCategoriesOfUser(userId)
    }

    fun getAllTemplatesOfUser(userId: Int): LiveData<List<TransactionTemplate>> {
        return templateDao.getTemplatesForUser(userId)
    }
}