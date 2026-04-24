package com.example.hcmus_quickhelper.features.payment.repository

import com.example.hcmus_quickhelper.features.payment.datasource.PaymentDataSource
import com.example.hcmus_quickhelper.features.payment.model.Payment
import com.example.hcmus_quickhelper.features.payment.model.PaymentInsert

class PaymentRepository (
    val paymentDataSource: PaymentDataSource
) {
    suspend fun getPaymentById(id: Int): Payment? {
        return paymentDataSource.getById(id)
    }

    suspend fun getPaymentByIdFullData(id: Int): Payment {
        return paymentDataSource.getByIdFullData(id)
    }

    suspend fun getPaymentByBookingId(id: Int): Payment? {
        return paymentDataSource.getByBookingId(id)
    }

    suspend fun getPaymentByBookingIdFullData(bookingId: Int): Payment {
        return paymentDataSource.getByBookingIdFullData(bookingId)
    }

    suspend fun getAllPayments(): List<Payment> {
        return paymentDataSource.getAll()
    }

    suspend fun getAllPaymentsFullData(): List<Payment> {
        return paymentDataSource.getAllFullData()
    }

    suspend fun insertPayment(payment: PaymentInsert): Payment {
        return paymentDataSource.insert(payment)
    }

    suspend fun updatePayment(paymentId: Int ,payment: PaymentInsert): Payment {
        return paymentDataSource.update(paymentId, payment)
    }
}