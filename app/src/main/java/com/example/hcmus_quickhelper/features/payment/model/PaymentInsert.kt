package com.example.hcmus_quickhelper.features.payment.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentInsert (
    @SerialName("amount")
    val amount: Double,

    @SerialName("method")
    val method: String,

    @SerialName("status")
    val status: String,

    @SerialName("booking_id")
    val bookingId: Int = 0,

    @SerialName("voucher_id")
    val voucherId: Int?,
)

fun Payment.toPaymentInsert(): PaymentInsert {
    return PaymentInsert(
        amount = this.amount,
        method = this.method,
        status = this.status,
        voucherId = this.voucherId,
    )
}