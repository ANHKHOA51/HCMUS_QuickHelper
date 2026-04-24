package com.example.hcmus_quickhelper.features.admin_statistic.model
import  kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class StatUser(
    val id: Int,
    val role: String,
    val fullname: String,
    val rating: Double? = 0.0
)

@Serializable
data class StatService(
    val id: Int
)

@Serializable
data class StatPayment(
    val id: Int,
    val amount: Double,
    @SerialName("booking_id") val bookingId: Int?,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class StatBooking(
    val id: Int,
    @SerialName("helper_id") val helperId: Int?
)