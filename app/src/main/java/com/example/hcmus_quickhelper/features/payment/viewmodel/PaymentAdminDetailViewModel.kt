package com.example.hcmus_quickhelper.features.payment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.features.payment.model.Payment
import com.example.hcmus_quickhelper.features.payment.repository.PaymentRepository
import kotlinx.coroutines.launch

class PaymentAdminDetailViewModel (
    private val repository: PaymentRepository
) : ViewModel() {
    private val _payment = MutableLiveData<Payment>()
    val payment: LiveData<Payment> = _payment

    fun loadPayment(paymentId: Int) {
        viewModelScope.launch {
            try {
                val data = repository.getPaymentByIdFullData(paymentId)
                _payment.value = data
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}