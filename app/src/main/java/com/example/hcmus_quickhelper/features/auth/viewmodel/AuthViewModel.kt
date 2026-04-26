package com.example.hcmus_quickhelper.features.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.core.model.User
import com.example.hcmus_quickhelper.features.auth.repository.AuthRepository
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    val loginResult = MutableLiveData<Result<User>?>()
    val registerResult = MutableLiveData<Result<Unit>?>()
    
    private val _sendOtpResult = MutableLiveData<Result<Unit>?>()
    val sendOtpResult: LiveData<Result<Unit>?> = _sendOtpResult

    private val _verifyResult = MutableLiveData<Result<Unit>?>()
    val verifyResult: LiveData<Result<Unit>?> = _verifyResult
    
    val isLoading = MutableLiveData<Boolean>(false)

    private val _correctOtp = MutableLiveData<String>()
    val correctOtp: LiveData<String> = _correctOtp

    fun setGeneratedOtp(otp: String) {
        _correctOtp.value = otp
    }

    fun login(identifier: String, pass: String, fcmToken: String?) {
        viewModelScope.launch {
            isLoading.value = true
            loginResult.value = repository.login(identifier, pass, fcmToken)
            isLoading.value = false
        }
    }

    fun signInWithGoogle(idToken: String, fcmToken: String?) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                // Decode email from Google ID Token
                // Note: In a real app, use a proper JWT library or Google's API to verify the token.
                // For this migration, we extract the email from the payload.
                val email = decodeGoogleEmail(idToken)
                loginResult.value = repository.loginWithGoogle(email, fcmToken)
            } catch (e: Exception) {
                loginResult.value = Result.failure(e)
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun decodeGoogleEmail(idToken: String): String {
        // Simple extraction for demonstration. Replace with secure decoding.
        val parts = idToken.split(".")
        if (parts.size < 2) throw Exception("Invalid ID Token")
        val payload = String(android.util.Base64.decode(parts[1], android.util.Base64.DEFAULT))
        val emailRegex = "\"email\":\"([^\"]+)\"".toRegex()
        return emailRegex.find(payload)?.groupValues?.get(1) ?: throw Exception("Email not found in token")
    }

    fun register(email: String, pass: String, fullname: String, phone: String, username: String? = null, role: String = "CUSTOMER") {
        viewModelScope.launch {
            isLoading.value = true
            registerResult.value = repository.register(email, pass, fullname, phone, username, role)
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

    fun verifyOldPassword(oldPass: String) {
        viewModelScope.launch {
            isLoading.value = true
            _verifyResult.value = repository.verifyOldPassword(oldPass)
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
