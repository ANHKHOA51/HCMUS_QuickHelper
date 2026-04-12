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
    var amount: Double,

    @SerialName("method")
    var method: String,

    @SerialName("status")
    var status: String,

    @SerialName("booking_id")
    var bookingId: Int,

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

enum class PaymentMethod(val displayName: String) {
    CASH("Tiền mặt"),
    BANK_TRANSFER("Chuyển khoản ngân hàng"),
    CREDIT_CARD("Thẻ tín dụng"),
    MOMO("Ví MoMo");

    companion object {
        fun fromString(value: String): PaymentMethod {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: CASH
        }
    }
}
