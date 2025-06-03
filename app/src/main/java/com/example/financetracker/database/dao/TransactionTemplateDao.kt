package com.example.financetracker.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.financetracker.database.model.TransactionTemplate

@Dao
interface TransactionTemplateDao {
    @Insert
    suspend fun insert(template: TransactionTemplate)

    @Delete
    suspend fun delete(template: TransactionTemplate)

    @Update
    suspend fun update(template: TransactionTemplate)

    @Query("SELECT * FROM transaction_templates")
    fun getAllTemplates(): LiveData<List<TransactionTemplate>>

    @Query("SELECT * FROM transaction_templates WHERE userId = :userId")
    fun getTemplatesForUser(userId: Int): LiveData<List<TransactionTemplate>>
}