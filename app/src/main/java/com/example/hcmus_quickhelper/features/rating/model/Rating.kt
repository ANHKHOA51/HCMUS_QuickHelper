package com.example.hcmus_quickhelper.features.rating.model

data class Rating(
    val id: String,
    val point: Float,
    val comment: String,

    val bookingId: String,

    val createdAt: String
)