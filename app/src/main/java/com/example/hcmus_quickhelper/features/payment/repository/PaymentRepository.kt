package com.example.hcmus_quickhelper.features.payment.repository

import com.example.hcmus_quickhelper.features.payment.datasource.MockPaymentDataSource
import com.example.hcmus_quickhelper.features.payment.datasource.PaymentDataSource
import com.example.hcmus_quickhelper.features.payment.model.Payment

class PaymentRepository (
    val paymentDataSource: PaymentDataSource
) {
    suspend fun getPaymentById(id: Int): Payment {
        return paymentDataSource.getById(id)
    }

    suspend fun getAllPayments(): List<Payment> {
        return paymentDataSource.getAll()
    }
}