package com.example.hcmus_quickhelper.features.voucher.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CollectVoucherResponse (
    @SerialName("success")
    val success: Boolean,

    @SerialName("message")
    val message: String,
)