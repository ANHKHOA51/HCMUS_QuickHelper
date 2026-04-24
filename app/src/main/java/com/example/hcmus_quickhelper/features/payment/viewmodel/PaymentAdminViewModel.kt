package com.example.hcmus_quickhelper.features.payment.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.features.payment.model.Payment
import com.example.hcmus_quickhelper.features.payment.repository.PaymentRepository
import kotlinx.coroutines.launch

class PaymentAdminViewModel(
    private val paymentRepository: PaymentRepository
) :  ViewModel() {
    private val _payments = MutableLiveData<List<Payment>>()
    val payments: LiveData<List<Payment>> = _payments

    fun loadPayments() {
        viewModelScope.launch {
            try {
                val data = paymentRepository.getAllPaymentsFullData()
                _payments.value = data
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}