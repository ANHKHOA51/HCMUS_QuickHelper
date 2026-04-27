package com.example.hcmus_quickhelper.features.community.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentUI(
    @SerialName("id")
    val commentId: Int? = null,

    @SerialName("content")
    val commentContent: String? = null,

    @SerialName("commentor_name")
    val commentorName: String? = null,

    @SerialName("commentor_avt")
    val commentorAvt: String? = null,

    @SerialName("created_at")
    val commentTime: String? = null,
)
