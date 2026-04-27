package com.example.hcmus_quickhelper.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val fullname: String,
    val username: String? = null,
    val email: String,
    val phone: String,
    val password: String,
    val role: String,

    @SerialName("is_blocked")
    var isBLocked: Boolean,

    @SerialName("avatar_url")
    val avatarUrl: String? = null,

    val rating: Double? = null,

    @SerialName("created_at")
    val createdAt: String? = null
)

enum class UserRole {
    HELPER,
    CUSTOMER,
    ADMIN
}