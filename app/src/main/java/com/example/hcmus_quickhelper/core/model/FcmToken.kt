package com.example.hcmus_quickhelper.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FcmToken(
    @SerialName("user_id")
    val userId: Int,
    val token: String
)