package com.example.financetracker.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

object UserPreferences {
    private val USER_ID_KEY = intPreferencesKey("user_id")

    suspend fun saveUserId(context: Context, userId: Int) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId
        }
    }

    suspend fun loadUserId(context: Context): Int? {
        val prefs = context.dataStore.data.first()
        return prefs[USER_ID_KEY]
    }
}