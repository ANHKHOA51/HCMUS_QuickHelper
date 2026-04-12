package com.example.hcmus_quickhelper.features.payment.datasource

import com.example.hcmus_quickhelper.features.payment.model.Payment

class MockPaymentDataSource {
    val payments: List<Payment> = listOf()

    suspend fun getById(id: Int): Payment {
        return Payment(
            id = id,
            amount = 250000.0,
            method = "Credit Card",
            status = "Pending",
            bookingId = 1,
            voucherId = 2,
            createdAt = "2023-05-01T12:00:00"
        )
    }

    suspend fun getAll(): List<Payment> {
        return payments
    }
}