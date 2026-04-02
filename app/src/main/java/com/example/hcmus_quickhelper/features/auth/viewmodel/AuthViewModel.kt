package com.example.hcmus_quickhelper.features.auth.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.features.auth.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    val loginResult = MutableLiveData<Result<Unit>?>()
    val registerResult = MutableLiveData<Result<Unit>?>()
    val isLoading = MutableLiveData<Boolean>(false)

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            isLoading.value = true
            loginResult.value = repository.login(email, pass)
            isLoading.value = false
        }
    }

    fun register(email: String, pass: String, fullname: String, phone: String) {
        viewModelScope.launch {
            isLoading.value = true
            registerResult.value = repository.register(email, pass, fullname, phone)
            isLoading.value = false
        }
    }
}
