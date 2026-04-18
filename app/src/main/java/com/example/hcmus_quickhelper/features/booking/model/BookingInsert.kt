package com.example.hcmus_quickhelper.features.booking.model

import com.example.hcmus_quickhelper.core.model.Booking
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

fun Booking.toBookingInsert(): BookingInsert {
    return BookingInsert(
        schedule = this.schedule,
        address = this.address,
        customerId = this.customerId,
        serviceId = this.serviceId,
        status = this.status,
        quantity = this.quantity,
        totalPrice = this.totalPrice,
        note = this.note
    )
}