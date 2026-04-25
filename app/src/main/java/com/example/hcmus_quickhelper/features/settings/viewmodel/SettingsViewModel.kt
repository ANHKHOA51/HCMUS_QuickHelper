package com.example.hcmus_quickhelper.features.settings.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.core.auth.SessionManager
import com.example.hcmus_quickhelper.core.auth.UserPreferences
import com.example.hcmus_quickhelper.features.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository,
    context: Context
) : ViewModel() {
    
    private val userPreferences = UserPreferences(context)
    
    val language: Flow<String> = userPreferences.language

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            userPreferences.updateLanguage(language)
        }
    }

    suspend fun logout() {
        SessionManager.logout()
    }
}
