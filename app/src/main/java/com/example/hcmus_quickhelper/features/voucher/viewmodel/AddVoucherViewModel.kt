package com.example.hcmus_quickhelper.features.voucher.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.features.voucher.model.Voucher
import com.example.hcmus_quickhelper.features.voucher.model.VoucherInsert
import com.example.hcmus_quickhelper.features.voucher.repository.VoucherRepository
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddVoucherViewModel(
    private val voucherRepository: VoucherRepository
): ViewModel() {
    private val _voucher = MutableLiveData<Voucher?>()
    val voucher: LiveData<Voucher?> get() = _voucher

    fun loadVoucher(voucherId: Int) {
        viewModelScope.launch {
            try {
                val data = voucherRepository.getVoucherById(voucherId)
                Log.d("Voucher", data.toString())
                _voucher.value = data
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addVoucher(voucher: VoucherInsert) {
        viewModelScope.launch {
            try {
                withContext(NonCancellable) {
                    voucherRepository.insertVoucher(voucher)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateVoucher(voucherId: Int, voucher: VoucherInsert) {
        viewModelScope.launch {
            try {
                withContext(NonCancellable) {
                    voucherRepository.updateVoucher(voucherId, voucher)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}