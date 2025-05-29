package com.example.financetracker.database.converters

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.example.financetracker.database.model.CategoryType
import java.time.Instant

class Converters() {
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun dateToTimestamp(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }

    @TypeConverter
    fun fromCategoryType(value: String): CategoryType {
        return CategoryType.valueOf(value)
    }

    @TypeConverter
    fun categoryTypeToString(type: CategoryType): String {
        return type.name
    }
}