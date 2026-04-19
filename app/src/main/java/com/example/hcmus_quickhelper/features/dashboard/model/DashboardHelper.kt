package com.example.hcmus_quickhelper.features.dashboard.model

import com.example.hcmus_quickhelper.core.model.Booking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DashboardHelper (

    val bookings: List<Booking>,

    @SerialName("rating")
    val rating: Double
)