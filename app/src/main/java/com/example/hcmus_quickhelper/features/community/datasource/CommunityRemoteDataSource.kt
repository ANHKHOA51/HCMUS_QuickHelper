package com.example.hcmus_quickhelper.features.community.datasource

import com.example.hcmus_quickhelper.core.database.SupabaseClient
import com.example.hcmus_quickhelper.features.chat.model.Message
import com.example.hcmus_quickhelper.features.community.model.Comment
import com.example.hcmus_quickhelper.features.community.model.Feed
import com.example.hcmus_quickhelper.features.community.model.FeedDetail
import com.example.hcmus_quickhelper.features.community.model.ToggleLikeResponse
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class CommunityRemoteDataSource {
    suspend fun getFeeds(userId: Int): List<Feed> {
        val params = mapOf("p_user_id" to userId)
        return SupabaseClient.client.postgrest.rpc("get_feeds", params)
            .decodeList<Feed>()
    }

    suspend fun getPopularFeeds(userId: Int): List<Feed> {
        val params = mapOf("p_user_id" to userId)
        return SupabaseClient.client.postgrest.rpc("get_popular_feeds", params)
            .decodeList<Feed>()
    }

    suspend fun getFeedDetail(feedId: Int, userId: Int): List<FeedDetail> {
        val params = mapOf("selected_feed_id" to feedId,
            "p_user_id" to userId)

        return SupabaseClient.client.postgrest.rpc("get_feed_detail_with_comments", params)
            .decodeList<FeedDetail>()
    }

    suspend fun postComment(feedId: Int, userId: Int, content: String): Comment {
        return SupabaseClient.client.postgrest
            .from("comments")
            .insert(Comment(feedId, userId, content)) {
                select()
            }
                .decodeSingle()
    }

    suspend fun toggleLike(feedId: Int, userId: Int): Pair<Boolean, Int> {
        val result = SupabaseClient.client.postgrest.rpc(
            "toggle_like",
            mapOf(
                "p_feed_id" to feedId,
                "p_user_id" to userId
            )
        ).decodeSingle<ToggleLikeResponse>()

        return Pair(result.liked, result.like_count)
    }
}