package com.example.hcmus_quickhelper.features.payment.repository

import com.example.hcmus_quickhelper.features.payment.model.PaymentModel

class MockPaymentRepository {
    fun getPaymentById(id: String): PaymentModel {
        return PaymentModel(
            id = id,
            amount = 250000,
            method = "Credit Card",
            status = "Pending",
            address = "Vincom Central Park, Bình Thạnh, TP.HCM",
            createdAt = "2023-05-01T12:00:00",
            updatedAt =  "2023-05-01T12:00:00",
            service = "Do dẹp nhà cửa - Goí chuyển nhà",
            helper = "Nguyễn thị hoa"
        )
    }
}