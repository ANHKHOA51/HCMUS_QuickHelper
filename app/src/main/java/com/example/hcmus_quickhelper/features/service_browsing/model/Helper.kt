package com.example.hcmus_quickhelper.features.service_browsing.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
data class Helper(
    val id: Int,
    val name: String,
    val avatarUrl: String?,
    val isOnline: Boolean = true,
    val rating: Double,
    val skills: String,
    val price: Double
)

// DTO
@Serializable
data class ServiceDto(
    val id: Int,
    val name: String,
    @SerialName("base_price") val basePrice: Double
)

@Serializable
data class HelperWithServicesDto(
    val id: Int,
    val fullname: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    val rating: Double? = 0.0,
    val services: List<ServiceDto> = emptyList() // Supabase tự động map list services qua bảng user_services
)