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
        SupabaseClient.client.postgrest.from("feeds")
            .delete {
                filter {
                    eq("id", feedId)
                }
            }
    }

    suspend fun deleteComment(commentId: Int) {
        SupabaseClient.client.postgrest.from("comments")
            .delete {
                filter {
                    eq("id", commentId)
                }
            }
    }

    suspend fun resetPassword(userId: Int, newPassword: String) {
        SupabaseClient.client.postgrest.from("users")
            .update({
                set("password", newPassword) // Bạn có thể thay đổi mật khẩu mặc định tại đây
            }) {
                filter {
                    eq("id", userId)
                }
            }
    }

    suspend fun toggleBlockUser(userId: Int, isCurrentlyBlocked: Boolean) {
        SupabaseClient.client.postgrest.from("users")
            .update({
                // Dùng dấu ! để đảo ngược: true thành false, false thành true
                set("is_blocked", !isCurrentlyBlocked)
            }) {
                filter {
                    eq("id", userId)
                }
            }
    }
}