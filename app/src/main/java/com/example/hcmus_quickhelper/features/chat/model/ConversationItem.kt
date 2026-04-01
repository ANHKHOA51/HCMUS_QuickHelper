package com.example.hcmus_quickhelper.features.chat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConversationItem(
    @SerialName("conversation_id")
    val conversationId: Int,

    @SerialName("latest_message")
    val latestMessage: String,

    @SerialName("sender_id")
    val senderId: Int,

    @SerialName("customer_id")
    val customerId: Int,

    @SerialName("customer_name")
    val customerName: String,

    @SerialName("customer_avt")
    val customerAvt: String?,

    @SerialName("helper_id")
    val helperId: Int,

    @SerialName("helper_name")
    val helperName: String,

    @SerialName("helper_avt")
    val helperAvt: String?,

    @SerialName("is_read")
    val isRead: Boolean,

    @SerialName("last_message_time")
    val lastMessageTime: String
)