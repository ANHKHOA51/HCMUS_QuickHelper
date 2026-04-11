package com.example.hcmus_quickhelper.features.payment.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.core.model.Booking
import com.example.hcmus_quickhelper.features.booking.repository.BookingRepository
import com.example.hcmus_quickhelper.features.payment.datasource.MockPaymentDataSource
import com.example.hcmus_quickhelper.features.payment.model.Payment
import com.example.hcmus_quickhelper.features.payment.model.PaymentStatus
import com.example.hcmus_quickhelper.features.payment.repository.PaymentRepository
import com.example.hcmus_quickhelper.features.voucher.model.Voucher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PaymentViewModel (
    private val paymentRepository: PaymentRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {
    private val _payment = MutableLiveData<Payment?>(null)
    val payment: LiveData<Payment?> = _payment

    private val _voucher = MutableLiveData<Voucher?>(null)
    val voucher: LiveData<Voucher?> = _voucher

    private  val _booking = MutableLiveData<Booking?>(null)
    val booking: LiveData<Booking?> = _booking

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadBooking(bookingId: Int) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val bookingData = bookingRepository.getBookingById(bookingId)
                val paymentData = paymentRepository.getPaymentByBookingId(bookingId)

                _booking.value = bookingData
                _payment.value = paymentData
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setVoucher(voucher: Voucher?) {
        _voucher.value = voucher
        _payment.value?.let{
            it.voucherId = voucher?.id
            _payment.value = it
        }
    }

    fun calcTotalPrice(): Double {
        val servicePrice = _booking.value?.totalPrice ?: 0.0

        val discount = _voucher.value?.discount ?: 0.0

        val total = servicePrice - discount

        return total.coerceAtLeast(0.0)
    }

    fun savePayment(method: String) {
        viewModelScope.launch {
            _payment.value?.let{
                it.method = method;
                it.amount = calcTotalPrice()
                it.status = PaymentStatus.SUCCESS.toString()
                paymentRepository.updatePayment(it)
            }
        }
    }
}