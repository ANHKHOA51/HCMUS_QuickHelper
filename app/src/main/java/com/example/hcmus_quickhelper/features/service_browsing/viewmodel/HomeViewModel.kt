package com.example.hcmus_quickhelper.features.service_browsing.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hcmus_quickhelper.core.model.User
import com.example.hcmus_quickhelper.features.service_browsing.model.Helper
import com.example.hcmus_quickhelper.features.service_browsing.model.Voucher
import com.example.hcmus_quickhelper.features.service_browsing.repository.HomeRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HomeRepository) : ViewModel() {

    private val _userProfile = MutableLiveData<User>()
    val userProfile: LiveData<User> = _userProfile

    private val _vouchers = MutableLiveData<List<Voucher>>()
    val vouchers: LiveData<List<Voucher>> = _vouchers

    private val _topHelpers = MutableLiveData<List<Helper>>()
    val topHelpers: LiveData<List<Helper>> = _topHelpers

    fun loadHomeData() {
        viewModelScope.launch {
            try {
                _userProfile.value = repository.getUserProfile()
                _vouchers.value = repository.getVouchers()
                _topHelpers.value = repository.getTopHelpers()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}