package com.example.hcmus_quickhelper.features.settings.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.core.auth.SessionManager
import com.example.hcmus_quickhelper.features.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {
    
    val language: Flow<String> = repository.language
    val pushNotificationsEnabled: Flow<Boolean> = repository.pushNotificationsEnabled
    val emailNotificationsEnabled: Flow<Boolean> = repository.emailNotificationsEnabled

    suspend fun updateLanguage(language: String) {
        repository.updateLanguage(language)
    }

    fun togglePushNotifications(enabled: Boolean) {
        viewModelScope.launch {
            repository.setPushNotificationsEnabled(enabled)
        }
    }

    fun toggleEmailNotifications(enabled: Boolean) {
        viewModelScope.launch {
            repository.setEmailNotificationsEnabled(enabled)
        }
    }

    suspend fun clearFcmTokenOnServer() {
        // Ensure we get the ID before anything clears the session
        val userId = SessionManager.currentUser.value?.id
        // Only proceed if userId is valid and not 0 (common default for uninitialized ints)
        if (userId != null && userId != 0) {
            try {
                repository.deleteFcmToken(userId)
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Failed to delete FCM token", e)
            }
        }
    }

    suspend fun logout() {
        SessionManager.logout()
    }
}
