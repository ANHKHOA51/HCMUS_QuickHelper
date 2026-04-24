package com.example.hcmus_quickhelper.features.voucher.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.features.service_browsing.model.Voucher
import com.example.hcmus_quickhelper.features.voucher.model.VoucherInsert
import com.example.hcmus_quickhelper.features.voucher.repository.VoucherRepository
import kotlinx.coroutines.launch

class AddVoucherViewModel(
    private val voucherRepository: VoucherRepository
): ViewModel() {

    fun addVoucher(voucher: VoucherInsert) {
        viewModelScope.launch {
            voucherRepository.insertVoucher(voucher)
        }
    }
}