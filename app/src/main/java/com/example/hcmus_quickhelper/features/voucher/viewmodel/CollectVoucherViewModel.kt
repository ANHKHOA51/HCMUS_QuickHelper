package com.example.hcmus_quickhelper.features.voucher.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.features.voucher.model.Voucher
import com.example.hcmus_quickhelper.features.voucher.repository.VoucherRepository
import kotlinx.coroutines.launch

class CollectVoucherViewModel (
    private val voucherRepository: VoucherRepository
) : ViewModel() {
    private val _collectibleVouchers = MutableLiveData<List<Voucher>>()
    val collectibleVouchers: LiveData<List<Voucher>> = _collectibleVouchers

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadVouchers(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val data = voucherRepository.getAllVoucherCollectible(userId)
                _collectibleVouchers.value = data
            } catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun collectVoucher(voucherId: Int, userId: Int) {
        val response = voucherRepository.collectVoucher(voucherId, userId)
        if(response.success) {
            Log.d("TEST", "SUCCESS")
        }
        else {
            Log.d("TEST", "FAILED")
        }
    }
}