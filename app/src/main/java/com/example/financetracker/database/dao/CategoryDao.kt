package com.example.financetracker.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.financetracker.database.model.Category

@Dao
interface CategoryDao {
    @Insert
    suspend fun insert(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Update
    suspend fun update(category: Category)

    @Query("SELECT * FROM categories")
    fun getAllCategories(): LiveData<List<Category>>
}