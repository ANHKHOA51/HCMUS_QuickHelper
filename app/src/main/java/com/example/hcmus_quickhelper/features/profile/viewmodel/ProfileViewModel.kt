package com.example.hcmus_quickhelper.features.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.core.auth.SessionManager
import com.example.hcmus_quickhelper.core.model.User
import com.example.hcmus_quickhelper.features.profile.repository.ProfileRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _updateStatus = MutableLiveData<Result<User>?>()
    val updateStatus: LiveData<Result<User>?> = _updateStatus

    fun saveProfile(username: String, fullname: String) {
        val currentUser = SessionManager.currentUser.value ?: return
        
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val updatedUser = currentUser.copy(username = username, fullname = fullname)
                val result = repository.updateUserInfo(updatedUser)
                _updateStatus.postValue(Result.success(result))
            } catch (e: Exception) {
                _updateStatus.postValue(Result.failure(e))
            } finally {
                _isLoading.postValue(false) // Ensures the UI stops loading
            }
        }
    }

    fun resetUpdateStatus() { _updateStatus.value = null }
}
