package com.example.hcmus_quickhelper.features.booking.model

data class BookingHistory(
    val id: String,
    val serviceName: String,
    val status: String,
    val priceText: String,
    val packageType: String,
    val date: String,
    val time: String
)