package com.example.snapstory.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")
class UserSessionPreference private constructor(private val dataStore: DataStore<Preferences>) {
    companion object {
        @Volatile
        private var INSTANCE: UserSessionPreference? = null
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_SESSION_ACTIVE = booleanPreferencesKey("isSessionActive")
        fun getInstance(dataStore: DataStore<Preferences>): UserSessionPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserSessionPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

    fun getUserSession(): Flow<UserSessionModel> {
        return dataStore.data.map { preferences ->
            UserSessionModel(
                preferences[EMAIL_KEY] ?: "",
                preferences[TOKEN_KEY] ?: "",
                preferences[IS_SESSION_ACTIVE] ?: false
            )
        }
    }

    fun getUserToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }
    }

    suspend fun getUserData(): UserSessionModel{
        val preferences = dataStore.data.first()
        return UserSessionModel(
            preferences[EMAIL_KEY] ?: "",
            preferences[TOKEN_KEY] ?: "",
            preferences[IS_SESSION_ACTIVE] ?: false
        )
    }

    suspend fun saveUserSession(userSession: UserSessionModel) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = userSession.email
            preferences[TOKEN_KEY] = userSession.token
            preferences[IS_SESSION_ACTIVE] ?: true
        }
    }

    suspend fun clearUserSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

}