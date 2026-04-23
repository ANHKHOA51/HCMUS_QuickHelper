package com.example.hcmus_quickhelper.features.voucher.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.features.voucher.model.Voucher
import com.example.hcmus_quickhelper.features.voucher.repository.VoucherRepository
import kotlinx.coroutines.launch

class VoucherManagementViewModel (
    private val voucherRepository: VoucherRepository
) : ViewModel() {
    private val _vouchers = MutableLiveData<List<Voucher>>()
    val vouchers: LiveData<List<Voucher>> = _vouchers

    fun loadVouchers() {
        viewModelScope.launch {
            try {
                val data = voucherRepository.getAllVoucherGlobal()
                _vouchers.value = data
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteVoucher(voucherId: Int) {

    }
}