package com.example.hcmus_quickhelper.features.community.datasource

import com.example.hcmus_quickhelper.core.database.SupabaseClient
import com.example.hcmus_quickhelper.features.community.model.Feed
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc

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

}