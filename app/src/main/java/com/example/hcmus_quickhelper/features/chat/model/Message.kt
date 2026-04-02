package com.example.hcmus_quickhelper.features.chat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message (
    @SerialName("id")
    val messageId: Int,

    @SerialName("sender_id")
    val senderId: Int,

    @SerialName("message")
    val message: String,

    @SerialName("message_type")
    val messageType: String,

    @SerialName("is_read")
    val isRead: Boolean,

    @SerialName("created_at")
    val createdAt: String
)