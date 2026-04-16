package com.example.hcmus_quickhelper.features.booking.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookingInsert (
    @SerialName("schedule")
    val schedule: String,

    @SerialName("address")
    val address: String,

    @SerialName("customer_id")
    val customerId: Int,

    @SerialName("service_id")
    val serviceId: Int,

    @SerialName("status")
    val status: String = "pending",

    @SerialName("quantity")
    val quantity: Int = 1,

    @SerialName("total_price")
    val totalPrice: Double,

    @SerialName("note")
    val note: String? = null
)