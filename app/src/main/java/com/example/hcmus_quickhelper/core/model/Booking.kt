package com.example.hcmus_quickhelper.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BookingStatus(val value: String) {
    COMPLETED("Hoàn thành"),
    CONFIRMED("Đã xác nhận"),
    IN_PROGRESS("Đang thực hiện"),
    PENDING("Chờ xử lý"),

    REJECTED( "Đã từ chối");
}

@Serializable
data class Booking (
    @SerialName("id")
    val id: Int,

    @SerialName("schedule")
    val schedule: String,

    @SerialName("address")
    val address: String,

    @SerialName("customer_id")
    val customerId: Int,

    @SerialName("helper_id")
    val helperId: Int?,

    @SerialName("service_id")
    val serviceId: Int,

    @SerialName("status")
    val status: String,

    @SerialName("quantity")
    val quantity: Int,

    @SerialName("total_price")
    val totalPrice: Double,

    @SerialName("note")
    val note: String,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("customer")
    val customer: User? = null,

    @SerialName("helper")
    val helper: User? = null,

    @SerialName("services")
    val service: Service? = null,
)