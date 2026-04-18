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

    @SerialName("helper_id")
    val helperId:  Int? = null,

    @SerialName("service_id")
    val serviceId: Int,

    @SerialName("status")
    val status: String,

    @SerialName("quantity")
    val quantity: Int,

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
        helperId = this.helperId,
        serviceId = this.serviceId,
        status = "PENDING",
        quantity = this.quantity,
        totalPrice = this.totalPrice,
        note = this.note
    )
}