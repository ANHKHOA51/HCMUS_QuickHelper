package com.example.hcmus_quickhelper.features.service_browsing.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class Voucher(
    val id: String,
    val code: String,
    val title: String, // expired and discount
    val colorHex: String
)

// DTO map với bảng vouchers
@Serializable
data class VoucherDto(
    val id: Int,
    val code: String,
    val discount: Double
)

@Serializable
data class VoucherOwnerDto(
    @SerialName("owner_id") val ownerId: Int,
    @SerialName("voucher_id") val voucherId: Int,
    val vouchers: VoucherDto? = null
)