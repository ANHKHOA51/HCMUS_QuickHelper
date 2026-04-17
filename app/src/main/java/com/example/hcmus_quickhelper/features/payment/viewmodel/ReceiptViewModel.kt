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
    private val bookingRepository: BookingRepository,
    private val voucherRepository: VoucherRepository
) : ViewModel() {
    private val _payment = MutableLiveData<Payment?>(null)
    val payment: LiveData<Payment?> = _payment

    private val _voucher = MutableLiveData<Voucher?>(null)
    val voucher: LiveData<Voucher?> = _voucher

    private  val _booking = MutableLiveData<Booking?>(null)
    val booking: LiveData<Booking?> = _booking

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadData(paymentId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val paymentData = paymentRepository.getPaymentById(paymentId)
            _payment.value = paymentData

            paymentData?.let {
                val bookingData = bookingRepository.getBookingById(it.bookingId)
                _booking.value = bookingData

                it.voucherId?.let {
                    val voucherData = voucherRepository.getVoucherById(it)
                    _voucher.value = voucherData
                }
            }

            try {
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun calcTotalPrice(): Double {
        val servicePrice = _booking.value?.totalPrice ?: 0.0

        val discount = _voucher.value?.discount ?: 0.0

        val total = servicePrice - discount

        return total.coerceAtLeast(0.0)
    }
}