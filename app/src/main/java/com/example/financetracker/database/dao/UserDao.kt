package com.example.financetracker.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.financetracker.database.model.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getAllUsers(): LiveData<List<User>>

    @Query("SELECT * FROM users")
    suspend fun getAllUsersOnce() : List<User>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): User?
}