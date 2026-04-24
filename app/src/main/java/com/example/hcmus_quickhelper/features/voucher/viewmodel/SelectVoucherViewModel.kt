package com.example.hcmus_quickhelper.features.voucher.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.features.voucher.model.Voucher
import com.example.hcmus_quickhelper.features.voucher.repository.VoucherRepository
import kotlinx.coroutines.launch

class SelectVoucherViewModel (
    private val repository: VoucherRepository
) : ViewModel() {
    private val _vouchers = MutableLiveData<List<Voucher>>()
    val vouchers: LiveData<List<Voucher>> = _vouchers

    private val _voucher = MutableLiveData<Voucher>()
    val voucher: LiveData<Voucher> = _voucher


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadVouchers(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val data = repository.getVouchersByUserId(userId)
                _vouchers.value = data
            } catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    fun setVoucher(voucher: Voucher) {
        _voucher.value = voucher
    }
}