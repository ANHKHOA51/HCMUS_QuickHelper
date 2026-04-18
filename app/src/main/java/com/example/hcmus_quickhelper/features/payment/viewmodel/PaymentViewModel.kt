package com.example.hcmus_quickhelper.features.payment.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.features.payment.model.Payment
import com.example.hcmus_quickhelper.features.payment.model.PaymentInsert
import com.example.hcmus_quickhelper.features.payment.model.PaymentMethod
import com.example.hcmus_quickhelper.features.payment.model.PaymentStatus
import com.example.hcmus_quickhelper.features.payment.repository.PaymentRepository
import com.example.hcmus_quickhelper.features.voucher.model.Voucher
import kotlinx.coroutines.launch

class PaymentViewModel (
    private val paymentRepository: PaymentRepository
) : ViewModel() {
    private val _payment = MutableLiveData<Payment?>(null)
    val payment: LiveData<Payment?> = _payment

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadBooking(bookingId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val paymentData = paymentRepository.getPaymentByBookingIdFullData(bookingId)
                
                // GIỮ LẠI VOUCHER NẾU ĐÃ CHỌN Ở LOCAL TRƯỚC ĐÓ
                val currentVoucher = _payment.value?.voucher
                if (currentVoucher != null) {
                    paymentData.voucher = currentVoucher
                    paymentData.voucherId = currentVoucher.id
                }

                // Cập nhật giá tiền dựa trên dữ liệu mới load
                val servicePrice = paymentData.booking?.totalPrice ?: 0.0
                val discount = paymentData.voucher?.discount ?: 0.0
                paymentData.amount = (servicePrice - discount).coerceAtLeast(0.0)

                _payment.value = paymentData
            } catch (e: Exception) {
                if(e is NoSuchElementException) {
                    val paymentDataInsert = PaymentInsert(
                        bookingId = bookingId,
                        amount = 0.0,
                        status = PaymentStatus.PENDING.toString(),
                        method = PaymentMethod.CASH.toString(),
                        voucherId = null
                    )
                    paymentRepository.insertPayment(paymentDataInsert)
                } else {
                    e.printStackTrace()
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setVoucher(voucher: Voucher?) {
        val currentPayment = _payment.value ?: return
        val servicePrice = currentPayment.booking?.totalPrice ?: 0.0

        val newAmount: Double
        val finalVoucher: Voucher?

        if (voucher == null) {
            newAmount = servicePrice
            finalVoucher = null
        } else {
            if (servicePrice >= (voucher.minPrice)) {
                val discount = voucher.discount
                newAmount = (servicePrice - discount).coerceAtLeast(0.0)
                finalVoucher = voucher
            } else {
                newAmount = servicePrice
                finalVoucher = null
            }
        }

        _payment.value = currentPayment.copy(
            voucherId = finalVoucher?.id,
            voucher = finalVoucher,
            amount = newAmount
        )
    }

    fun calcTotalPrice(): Double {
        return _payment.value?.amount ?: 0.0
    }
}
