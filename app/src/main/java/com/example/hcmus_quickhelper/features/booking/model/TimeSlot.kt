package com.example.hcmus_quickhelper.features.booking.model

data class TimeSlot(
    val id: String,
    val timeText: String,
    var isSelected: Boolean = false
)