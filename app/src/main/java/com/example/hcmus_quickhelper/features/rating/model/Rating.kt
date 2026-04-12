package com.example.hcmus_quickhelper.features.rating.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Rating(
    @SerialName("id")
    val id: Int? = null,

    @SerialName("point")
    val point: Int,

    @SerialName("comment")
    val comment: String,

    @SerialName("booking_id")
    val bookingId: Int,

    @SerialName("created_at")
    val createdAt: String? = null
)