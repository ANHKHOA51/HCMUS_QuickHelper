package com.example.hcmus_quickhelper.features.booking.model

import com.example.hcmus_quickhelper.core.model.BookingStatus

data class BookingHistory(
    val id: Int,
    val serviceName: String,
    val statusEnum: BookingStatus,
    val priceText: String,
    val packageType: String,
    val date: String,
    val time: String
)