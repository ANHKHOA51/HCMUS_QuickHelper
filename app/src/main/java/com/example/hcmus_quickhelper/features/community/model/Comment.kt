package com.example.hcmus_quickhelper.features.community.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    @SerialName("feed_id")
    val feedId: Int,

    @SerialName("commentor_id")
    val commentorId: Int,

    @SerialName("content")
    val content: String,
)
