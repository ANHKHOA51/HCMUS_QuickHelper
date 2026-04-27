package com.example.hcmus_quickhelper.features.admin_management.datasource

import android.util.Log
import com.example.hcmus_quickhelper.core.database.SupabaseClient
import com.example.hcmus_quickhelper.core.model.User
import com.example.hcmus_quickhelper.features.community.model.Comment
import com.example.hcmus_quickhelper.features.community.model.Feed
import com.example.hcmus_quickhelper.features.community.model.FeedDetail
import com.example.hcmus_quickhelper.features.community.model.ToggleLikeResponse
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class ManagementDataSource {
    suspend fun getFeeds(): List<Feed> {
        val params = mapOf("p_user_id" to -1)
        return SupabaseClient.client.postgrest.rpc("get_feeds", params)
            .decodeList<Feed>()
    }

    suspend fun getFeedDetail(feedId: Int): List<FeedDetail> {
        val params = mapOf("selected_feed_id" to feedId,
            "p_user_id" to -1)

        return SupabaseClient.client.postgrest.rpc("get_feed_detail_with_comments", params)
            .decodeList<FeedDetail>()
    }

    suspend fun getUsers(): List<User> {
        return SupabaseClient.client.postgrest.from("users")
            .select()
            .decodeList<User>()
    }

    suspend fun deleteFeeds(feedId: Int) {

    }

    suspend fun resetPassword(userId: Int) {

    }

    suspend fun blockUser(userId: Int) {

    }
}