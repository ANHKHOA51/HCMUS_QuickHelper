package com.example.hcmus_quickhelper.features.community.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Feed(
    @SerialName("feed_id")
    val id: Int,

    @SerialName("feed_content")
    val content: String,

    @SerialName("owner_fullname")
    val ownerFullname: String,

    @SerialName("owner_username")
    val ownerUsername: String,

    @SerialName("owner_role")
    val ownerRole: String,

    @SerialName("owner_avt")
    val ownerAvatarUrl: String?,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("like_count")
    var likeCount: Int,

    @SerialName("comment_count")
    val commentCount: Int,

    @SerialName("is_like")
    var isLiked: Boolean
)