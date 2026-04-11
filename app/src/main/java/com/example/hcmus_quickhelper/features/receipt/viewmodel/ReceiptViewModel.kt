package com.example.hcmus_quickhelper.features.receipt.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hcmus_quickhelper.core.model.Booking
import com.example.hcmus_quickhelper.features.payment.model.Payment
import com.example.hcmus_quickhelper.features.voucher.model.Voucher

class ReceiptViewModel() : ViewModel() {
    private val _payment = MutableLiveData<Payment?>(null)
    val payment: LiveData<Payment?> = _payment

    private val _voucher = MutableLiveData<Voucher?>(null)
    val voucher: LiveData<Voucher?> = _voucher

    private  val _booking = MutableLiveData<Booking?>(null)
    val booking: LiveData<Booking?> = _booking

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun setPayment(payment: Payment?) {
        _payment.value = payment
    }

    fun setBooking(booking: Booking?) {
        _booking.value = booking
    }

    fun setVoucher(voucher: Voucher?) {
        _voucher.value = voucher
    }

    fun calcTotalPrice(): Double {
        val servicePrice = _booking.value?.totalPrice ?: 0.0

        val discount = _voucher.value?.discount ?: 0.0

        val total = servicePrice - discount

        return total.coerceAtLeast(0.0)
    }
}