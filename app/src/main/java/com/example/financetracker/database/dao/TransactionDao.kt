package com.example.financetracker.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.financetracker.database.model.Transaction

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions")
    fun getAllTransactions(): LiveData<List<Transaction>>
}