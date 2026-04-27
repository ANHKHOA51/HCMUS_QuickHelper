package com.example.hcmus_quickhelper.core.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.hcmus_quickhelper.core.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        private val USER_ID = intPreferencesKey("user_id")
        private val USER_FULLNAME = stringPreferencesKey("user_fullname")
        private val USER_USERNAME = stringPreferencesKey("user_username")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_PHONE = stringPreferencesKey("user_phone")
        private val USER_ROLE = stringPreferencesKey("user_role")
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val LANGUAGE = stringPreferencesKey("language")
        private val PUSH_NOTIFICATIONS = booleanPreferencesKey("push_notifications")
        private val EMAIL_NOTIFICATIONS = booleanPreferencesKey("email_notifications")
        private val IS_BLOCKED = booleanPreferencesKey("is_blocked")
    }

    val userFlow: Flow<User?> = context.dataStore.data.map { preferences ->
        if (preferences[IS_LOGGED_IN] == true) {
            User(
                id = preferences[USER_ID] ?: 0,
                fullname = preferences[USER_FULLNAME] ?: "",
                username = preferences[USER_USERNAME],
                email = preferences[USER_EMAIL] ?: "",
                phone = preferences[USER_PHONE] ?: "",
                password = "", // Do not store password
                role = preferences[USER_ROLE] ?: "",
                isBLocked = preferences[IS_BLOCKED] ?: false
            )
        } else {
            null
        }
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }

    val language: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE] ?: "English"
    }

    val pushNotificationsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PUSH_NOTIFICATIONS] ?: true
    }

    val emailNotificationsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[EMAIL_NOTIFICATIONS] ?: true
    }

    suspend fun saveSession(user: User) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = user.id
            preferences[USER_FULLNAME] = user.fullname
            preferences[USER_USERNAME] = user.username ?: ""
            preferences[USER_EMAIL] = user.email
            preferences[USER_PHONE] = user.phone
            preferences[USER_ROLE] = user.role
            preferences[IS_LOGGED_IN] = true
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_ID)
            preferences.remove(USER_FULLNAME)
            preferences.remove(USER_USERNAME)
            preferences.remove(USER_EMAIL)
            preferences.remove(USER_PHONE)
            preferences.remove(USER_ROLE)
            preferences[IS_LOGGED_IN] = false
        }
    }

    suspend fun updateLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE] = language
        }
    }

    suspend fun setPushNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PUSH_NOTIFICATIONS] = enabled
        }
    }

    suspend fun setEmailNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[EMAIL_NOTIFICATIONS] = enabled
        }
    }
}