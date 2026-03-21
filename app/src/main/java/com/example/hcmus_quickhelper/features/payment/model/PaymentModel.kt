package com.example.hcmus_quickhelper.features.payment.model

data class PaymentModel (
    val id: String,
    val amount: Int,
    val method: String,
    val status: String,
    val address: String,
    val createdAt: String,
    val updatedAt: String,


    val service: String,
    val helper: String,
)