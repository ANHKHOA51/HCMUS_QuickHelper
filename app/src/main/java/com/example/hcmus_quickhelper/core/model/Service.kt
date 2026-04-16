package com.example.hcmus_quickhelper.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Service(
    @SerialName("id")
    val id: Int? = null,

    @SerialName("name")
    val name: String,

    @SerialName("base_price")
    val basePrice: Double,

    @SerialName("unit_price")
    val unitPrice: Double? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null
)