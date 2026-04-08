package com.example.hcmus_quickhelper.features.payment.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.features.payment.datasource.MockPaymentDataSource
import com.example.hcmus_quickhelper.features.payment.model.Payment
import com.example.hcmus_quickhelper.features.payment.repository.PaymentRepository
import com.example.hcmus_quickhelper.features.voucher.model.Voucher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PaymentViewModel (
    private val paymentRepository: PaymentRepository
) : ViewModel() {
    private val _payment = MutableLiveData<Payment?>(null)
    val payment: LiveData<Payment?> = _payment

    private val _voucher = MutableLiveData<Voucher?>(null)
    var voucher: LiveData<Voucher?> = _voucher

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadPayment(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val data = paymentRepository.getPaymentById(id)
                Log.d("DATA", data.toString())
                _payment.value = data
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setVoucher(voucher: Voucher?) {
        _voucher.value = voucher
    }
}