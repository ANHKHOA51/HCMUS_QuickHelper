package com.example.hcmus_quickhelper.features.chat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message (
    @SerialName("id")
    val messageId: Int,

    @SerialName("conversation_id")
    val conversationId: Int,

    @SerialName("sender_id")
    val senderId: Int,

    @SerialName("message")
    val message: String,

    @SerialName("message_type")
    val messageType: String = "TEXT",

    @SerialName("is_read")
    val isRead: Boolean = false,

    @SerialName("created_at")
    val createdAt: String = ""
)