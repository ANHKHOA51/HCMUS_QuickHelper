package com.example.hcmus_quickhelper.features.payment.viewmodel

import androidx.lifecycle.ViewModel
import com.example.hcmus_quickhelper.features.payment.model.PaymentModel
import com.example.hcmus_quickhelper.features.payment.repository.MockPaymentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PaymentViewModel: ViewModel() {
    private val paymentRepository = MockPaymentRepository();

    private val _payment = MutableStateFlow(paymentRepository.getPaymentById(""));
    val payment: StateFlow<PaymentModel> = _payment.asStateFlow();

    fun updatePayment()  {
        _payment.update { currentState ->
            currentState.copy()
        }
    }
}