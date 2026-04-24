package com.example.hcmus_quickhelper.features.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.core.auth.SessionManager
import com.example.hcmus_quickhelper.core.model.User
import com.example.hcmus_quickhelper.features.profile.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

sealed class ProfileUiState {
    object Idle : ProfileUiState()
    object Loading : ProfileUiState()
    data class Success(val user: User) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState

    fun saveProfile(username: String, fullname: String, phone: String) {
        viewModelScope.launch {
            val currentUser = SessionManager.currentUser.value ?: return@launch
            
            _uiState.value = ProfileUiState.Loading
            try {
                val updatedUser = currentUser.copy(
                    username = username, 
                    fullname = fullname,
                    phone = phone
                )
                val result = repository.updateUserInfo(updatedUser)
                // Use updateCurrentUser as per sync plan
                SessionManager.updateCurrentUser(result)
                _uiState.value = ProfileUiState.Success(result)
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.localizedMessage ?: "Unknown Error")
            }
        }
    }

    fun resetToIdle() {
        _uiState.value = ProfileUiState.Idle
    }
}