package com.example.hcmus_quickhelper.features.payment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.core.model.Booking
import com.example.hcmus_quickhelper.features.booking.repository.BookingRepository
import com.example.hcmus_quickhelper.features.payment.model.Payment
import com.example.hcmus_quickhelper.features.payment.repository.PaymentRepository
import com.example.hcmus_quickhelper.features.voucher.model.Voucher
import com.example.hcmus_quickhelper.features.voucher.repository.VoucherRepository
import kotlinx.coroutines.launch

class ReceiptViewModel(
    private val paymentRepository: PaymentRepository,
) : ViewModel() {
    private val _payment = MutableLiveData<Payment?>(null)
    val payment: LiveData<Payment?> = _payment

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadData(paymentId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val paymentData = paymentRepository.getPaymentByIdFullData(paymentId)
                _payment.value = paymentData
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
