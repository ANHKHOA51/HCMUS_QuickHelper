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
import com.example.hcmus_quickhelper.features.payment.model.toPaymentInsert
import com.example.hcmus_quickhelper.features.payment.repository.PaymentRepository
import com.example.hcmus_quickhelper.features.voucher.model.Voucher
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                val finalPayment = try {
                    paymentRepository.getPaymentByBookingIdFullData(bookingId)
                } catch (e: NoSuchElementException) {
                    val newItem = PaymentInsert(
                        bookingId = bookingId,
                        amount = 0.0,
                        status = PaymentStatus.PENDING.toString(),
                        method = PaymentMethod.CASH.toString(),
                        voucherId = null
                    )
                    paymentRepository.insertPayment(newItem)
                    paymentRepository.getPaymentByBookingIdFullData(bookingId)
                }

                val servicePrice = finalPayment.booking?.totalPrice ?: 0.0
                val currentVoucher = _payment.value?.voucher
                val discount = currentVoucher?.discount ?: 0.0

                val calculatedAmount = (servicePrice - discount).coerceAtLeast(0.0)

                _payment.value = finalPayment.copy(
                    amount = calculatedAmount,
                    voucher = currentVoucher,
                    voucherId = currentVoucher?.id
                )

            } catch (e: Exception) {
                e.printStackTrace()
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

    fun savePayment() {
        viewModelScope.launch {
            try {
                withContext(NonCancellable) {
                    paymentRepository.updatePayment(
                        _payment.value!!.id!!,
                        _payment.value!!.toPaymentInsert()
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun submitPayment(method: String) {
        val currentPayment = _payment.value ?: return
        val updatedPayment = currentPayment.copy(
            method = PaymentMethod.fromDisplayName(method).toString(),
            status = PaymentStatus.SUCCESS.toString()
        )

        _payment.value = updatedPayment

        _payment.value?.let { payment ->
            viewModelScope.launch {
                paymentRepository.updatePayment(payment.id!!, payment.toPaymentInsert())
            }
        }
    }

    fun calcTotalPrice(): Double {
        return _payment.value?.amount ?: 0.0
    }
}
