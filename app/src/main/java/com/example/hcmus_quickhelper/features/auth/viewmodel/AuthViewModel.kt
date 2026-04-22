package com.example.hcmus_quickhelper.features.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.core.model.User
import com.example.hcmus_quickhelper.features.auth.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    val loginResult = MutableLiveData<Result<User>?>()
    val registerResult = MutableLiveData<Result<Unit>?>()
    
    private val _sendOtpResult = MutableLiveData<Result<Unit>?>()
    val sendOtpResult: LiveData<Result<Unit>?> = _sendOtpResult

    private val _verifyResult = MutableLiveData<Result<Unit>?>()
    val verifyResult: LiveData<Result<Unit>?> = _verifyResult
    
    val isLoading = MutableLiveData<Boolean>(false)

    // State to hold the correct OTP for local verification if needed
    private val _correctOtp = MutableLiveData<String>()
    val correctOtp: LiveData<String> = _correctOtp

    fun setGeneratedOtp(otp: String) {
        _correctOtp.value = otp
    }

    fun login(email: String, pass: String, fcmToken: String?) {
        viewModelScope.launch {
            isLoading.value = true
            loginResult.value = repository.login(email, pass, fcmToken)
            isLoading.value = false
        }
    }

    fun signInWithGoogle(idToken: String, fcmToken: String?) {
        viewModelScope.launch {
            isLoading.value = true
            loginResult.value = repository.loginWithGoogle(idToken, fcmToken)
            isLoading.value = false
        }
    }

    fun register(email: String, pass: String, fullname: String, phone: String, username: String? = null) {
        viewModelScope.launch {
            isLoading.value = true
            registerResult.value = repository.register(email, pass, fullname, phone, username)
            isLoading.value = false
        }
    }

    fun sendOtp(email: String) {
        viewModelScope.launch {
            isLoading.value = true
            _sendOtpResult.value = repository.sendOtp(email)
            isLoading.value = false
        }
    }

    fun verifyOtp(email: String, token: String) {
        viewModelScope.launch {
            isLoading.value = true
            _verifyResult.value = repository.verifyEmailOtp(email, token)
            isLoading.value = false
        }
    }

    fun resetVerifyResult() {
        _verifyResult.value = null
    }

    fun resetSendOtpResult() {
        _sendOtpResult.value = null
    }
}
