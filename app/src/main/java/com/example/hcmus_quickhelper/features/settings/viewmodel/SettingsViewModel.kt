package com.example.hcmus_quickhelper.features.settings.viewmodel

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

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            repository.updateLanguage(language)
        }
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

    suspend fun logout() {
        SessionManager.logout()
    }
}
