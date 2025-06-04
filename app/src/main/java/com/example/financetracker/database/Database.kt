package com.example.financetracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.financetracker.database.converters.Converters
import com.example.financetracker.database.dao.CategoryDao
import com.example.financetracker.database.dao.TransactionDao
import com.example.financetracker.database.dao.TransactionTemplateDao
import com.example.financetracker.database.dao.UserDao
import com.example.financetracker.database.model.Transaction
import com.example.financetracker.database.model.Category
import com.example.financetracker.database.model.TransactionTemplate
import com.example.financetracker.database.model.User

@TypeConverters(Converters::class)
@Database(entities = [Transaction::class, Category::class, User::class, TransactionTemplate::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun userDao(): UserDao
    abstract fun templateDao(): TransactionTemplateDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finance_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}