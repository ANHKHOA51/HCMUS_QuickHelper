package com.example.hcmus_quickhelper.features.settings.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.core.auth.SessionManager
import com.example.hcmus_quickhelper.features.auth.repository.AuthRepository
import kotlinx.coroutines.launch

sealed class ChangePasswordUiState {
    object Idle : ChangePasswordUiState()
    object Loading : ChangePasswordUiState()
    object Verified : ChangePasswordUiState()
    object Success : ChangePasswordUiState()
    data class Error(val message: String) : ChangePasswordUiState()
}

class ChangePasswordViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableLiveData<ChangePasswordUiState>(ChangePasswordUiState.Idle)
    val uiState: LiveData<ChangePasswordUiState> = _uiState

    fun verifyOldPassword(oldPassword: String) {
        viewModelScope.launch {
            _uiState.value = ChangePasswordUiState.Loading
            val result = repository.verifyOldPassword(oldPassword)
            result.fold(
                onSuccess = { _uiState.value = ChangePasswordUiState.Verified },
                onFailure = { error ->
                    _uiState.value = ChangePasswordUiState.Error(error.message ?: "Incorrect password")
                }
            )
        }
    }

    fun updatePassword(newPassword: String) {
        viewModelScope.launch {
            val user = SessionManager.currentUser.value ?: run {
                _uiState.value = ChangePasswordUiState.Error("User session not found")
                return@launch
            }
            _uiState.value = ChangePasswordUiState.Loading
            val result = repository.updatePassword(user.id, newPassword)
            result.fold(
                onSuccess = {
                    val updatedUser = user.copy(password = newPassword)
                    SessionManager.updateCurrentUser(updatedUser)
                    _uiState.value = ChangePasswordUiState.Success
                },
                onFailure = { error ->
                    _uiState.value = ChangePasswordUiState.Error(error.localizedMessage ?: "Failed to update password")
                }
            )
        }
    }

    fun updatePasswordByEmail(email: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.value = ChangePasswordUiState.Loading
            val result = repository.updatePasswordByEmail(email, newPassword)
            result.fold(
                onSuccess = { _uiState.value = ChangePasswordUiState.Success },
                onFailure = { error ->
                    _uiState.value = ChangePasswordUiState.Error(error.localizedMessage ?: "Failed to update password")
                }
            )
        }
    }

    fun resetToIdle() {
        _uiState.value = ChangePasswordUiState.Idle
    }
}
