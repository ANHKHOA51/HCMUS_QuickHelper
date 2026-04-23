package com.example.hcmus_quickhelper.features.community.model

import kotlinx.serialization.Serializable

@Serializable
data class ToggleLikeResponse(
    val liked: Boolean,
    val like_count: Int
)