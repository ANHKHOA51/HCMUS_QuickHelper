package com.example.hcmus_quickhelper.features.payment.repository

import com.example.hcmus_quickhelper.features.payment.datasource.MockPaymentDataSource
import com.example.hcmus_quickhelper.features.payment.model.Payment

class PaymentRepository (
    val paymentDataSource: MockPaymentDataSource
) {
    suspend fun getPaymentById(id: String): Payment {
        return paymentDataSource.getPaymentById(id)
    }
}