package com.example.hcmus_quickhelper.features.booking.model
import  kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BookingEvidence(
    @SerialName("booking_id") val bookingId: Int,
    @SerialName("evidence_url") val evidenceUrl: String,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class BookingConversation(
    val id: Int,
    @SerialName("booking_id") val bookingId: Int
)

@Serializable
data class ConversationInsert(
    @SerialName("booking_id") val bookingId: Int
)