package com.example.hcmus_quickhelper.features.voucher.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VoucherInsert (
    @SerialName("code")
    val code: String,

    @SerialName("quantity")
    val quantity: Int,

    @SerialName("discount")
    val discount: Double,

    @SerialName("min_price")
    val minPrice: Double,

    @SerialName("expired_at")
    val expiredAt: String,
)