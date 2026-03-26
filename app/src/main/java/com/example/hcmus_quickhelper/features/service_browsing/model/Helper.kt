package com.example.hcmus_quickhelper.features.service_browsing.model

data class Helper (
    val id: String,
    val name: String,
    val avatarUrl: String,
    val isOnline: Boolean,
    val isVerified: Boolean,
    val rating: Double,
    val reviewCount: Int,
    val skills: String,
    val distance: Double,
    val priceText: String
)