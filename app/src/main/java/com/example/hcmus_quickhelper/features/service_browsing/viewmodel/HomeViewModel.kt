package com.example.hcmus_quickhelper.features.service_browsing.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.core.auth.SessionManager
import com.example.hcmus_quickhelper.core.model.User
import com.example.hcmus_quickhelper.features.service_browsing.model.Helper
import com.example.hcmus_quickhelper.features.service_browsing.model.Voucher
import com.example.hcmus_quickhelper.features.service_browsing.repository.HomeRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HomeRepository) : ViewModel() {

    // Expose the global user from SessionManager to the Fragment
    val userProfile: LiveData<User?> = SessionManager.currentUser.asLiveData()

    private val _vouchers = MutableLiveData<List<Voucher>>()
    val vouchers: LiveData<List<Voucher>> = _vouchers

    private val _topHelpers = MutableLiveData<List<Helper>>()
    val topHelpers: LiveData<List<Helper>> = _topHelpers

    init {
        // Automatically react to session changes
        viewModelScope.launch {
            SessionManager.currentUser.collectLatest { user ->
                // This triggers the LiveData exposed above via asLiveData()
            }
        }
    }

    fun loadHomeData() {
        viewModelScope.launch {
            try {
                _vouchers.value = repository.getVouchers()
                _topHelpers.value = repository.getTopHelpers()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
