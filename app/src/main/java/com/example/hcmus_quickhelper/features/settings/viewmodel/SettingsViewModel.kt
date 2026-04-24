package com.example.hcmus_quickhelper.features.settings.viewmodel

import androidx.lifecycle.ViewModel
import com.example.hcmus_quickhelper.core.auth.SessionManager
import com.example.hcmus_quickhelper.features.settings.repository.SettingsRepository

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {
    
    suspend fun logout() {
        SessionManager.logout()
    }
}
