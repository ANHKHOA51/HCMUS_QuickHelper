package com.example.hcmus_quickhelper.features.service_browsing.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hcmus_quickhelper.core.model.User
import com.example.hcmus_quickhelper.features.service_browsing.model.Helper
import com.example.hcmus_quickhelper.features.service_browsing.model.Voucher
import com.example.hcmus_quickhelper.features.service_browsing.repository.HomeRepository
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.features.service_browsing.model.ServiceDto
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HomeRepository) : ViewModel() {
    private val currentUserId = 7

    private val _userProfile = MutableLiveData<User>()
    val userProfile: LiveData<User> get() = _userProfile

    private val _vouchers = MutableLiveData<List<Voucher>>()
    val vouchers: LiveData<List<Voucher>> get() = _vouchers

    private val _topHelpers = MutableLiveData<List<Helper>>()
    val topHelpers: LiveData<List<Helper>> get() = _topHelpers

    private val _services = MutableLiveData<List<ServiceDto>>()
    val services: LiveData<List<ServiceDto>> get() = _services

    fun loadHomeData() {
        viewModelScope.launch {
            repository.getUserProfile(currentUserId).onSuccess { _userProfile.value = it }
            repository.getVouchers(currentUserId).onSuccess { _vouchers.value = it }
            repository.getPopularServices().onSuccess { _services.value = it }
            repository.getTopHelpers().onSuccess { _topHelpers.value = it }
        }
    }
}