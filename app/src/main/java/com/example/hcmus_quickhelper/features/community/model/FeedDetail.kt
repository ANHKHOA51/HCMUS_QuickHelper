package com.example.hcmus_quickhelper.features.community.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeedDetail(
    // --- Thông tin của Bài viết (Feed) ---
    @SerialName("feed_id")
    val feedId: Int,

    @SerialName("feed_content")
    val feedContent: String,

    @SerialName("created_at")
    val createdAt: String, // Timestamp từ Supabase trả về dạng String ISO

    @SerialName("owner_fullname")
    val ownerFullname: String,

    @SerialName("owner_username")
    val ownerUsername: String?,

    @SerialName("owner_role")
    val ownerRole: String,

    @SerialName("owner_avt")
    val ownerAvatar: String?,

    // --- Thông tin của Bình luận (Comment) ---
    @SerialName("comment_id")
    val commentId: Int? = null,

    @SerialName("comment_content")
    val commentContent: String? = null,

    @SerialName("commentor_name")
    val commentorName: String? = null,

    @SerialName("commentor_avt")
    val commentorAvt: String? = null,

    @SerialName("comment_time")
    val commentTime: String? = null,

    // --- Thông tin tương tác (Metadata) ---
    @SerialName("like_count")
    val likeCount: Int,

    @SerialName("comment_count")
    val commentCount: Int,

    @SerialName("is_like")
    val isLiked: Boolean
)
