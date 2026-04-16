package com.example.hcmus_quickhelper.features.booking.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BookingRequest (
    @SerialName("id")
    val id: Int,

    @SerialName("schedule")
    val schedule: String,

    @SerialName("address")
    val address: String,

    @SerialName("customer_name")
    val customerName: String,

    @SerialName("customer_phone")
    val customerPhone: String,

    @SerialName("customer_avatar")
    val customerAvatar: String,

    @SerialName("customer_rating")
    val customerRating: Double,

    @SerialName("service_name")
    val serviceName: String,

    @SerialName("status")
    var status: String,

    @SerialName("total_price")
    val totalPrice: Double,

    @SerialName("created_at")
    val createdAt: String
)