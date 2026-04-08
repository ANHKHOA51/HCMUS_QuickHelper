package com.example.hcmus_quickhelper.features.voucher.model

import android.os.Parcelable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Voucher(
    @SerialName("id")
    val id: Int,

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

    @SerialName("created_at")
    val createdAt: String
)