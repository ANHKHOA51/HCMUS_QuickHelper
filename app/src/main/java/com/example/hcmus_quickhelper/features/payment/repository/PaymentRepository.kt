package com.example.hcmus_quickhelper.features.payment.repository

import com.example.hcmus_quickhelper.core.model.Booking
import com.example.hcmus_quickhelper.features.payment.datasource.MockPaymentDataSource
import com.example.hcmus_quickhelper.features.payment.datasource.PaymentDataSource
import com.example.hcmus_quickhelper.features.payment.model.Payment

class PaymentRepository (
    val paymentDataSource: PaymentDataSource
) {
    suspend fun getPaymentById(id: Int): Payment? {
        return paymentDataSource.getById(id)
    }

    suspend fun getPaymentByBookingId(id: Int): Payment? {
        return paymentDataSource.getByBookingId(id)
    }

    suspend fun getAllPayments(): List<Payment> {
        return paymentDataSource.getAll()
    }

    suspend fun insertPayment(payment: Payment): Payment {
        return paymentDataSource.insert(payment)
    }

    suspend fun updatePayment(payment: Payment): Payment {
        return paymentDataSource.update(payment)
    }
}