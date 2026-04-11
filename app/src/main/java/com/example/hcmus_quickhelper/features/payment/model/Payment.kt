package com.example.hcmus_quickhelper.features.payment.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Serializable
@Parcelize
data class Payment (
    @SerialName("id")
    val id: Int? = null,

    @SerialName("amount")
    val amount: Double,

    @SerialName("method")
    val method: String,

    @SerialName("status")
    val status: String,

    @SerialName("booking_id")
    val bookingId: Int,

    @SerialName("voucher_id")
    var voucherId: Int?,

    @SerialName("created_at")
    val createdAt: String? = null
): Parcelable

enum class PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    CANCELED
}