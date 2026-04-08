package com.example.hcmus_quickhelper.features.payment.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Payment (
    @SerialName("id")
    val id: Int,

    @SerialName("amount")
    val amount: Double,

    @SerialName("method")
    val method: String,

    @SerialName("status")
    val status: String,

    @SerialName("booking_id")
    val bookingId: Int,

    @SerialName("voucher_id")
    val voucherId: Int?,

    @SerialName("created_at")
    val createdAt: String,
)