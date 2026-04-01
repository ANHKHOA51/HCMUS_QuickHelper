package com.example.hcmus_quickhelper.features.chat.datasource

import com.example.hcmus_quickhelper.core.database.SupabaseClient
import com.example.hcmus_quickhelper.features.chat.model.ConversationItem
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc

class ChatRemoteDataSource {
    suspend fun getChatList(userId: Int): List<ConversationItem> {
        val params = mapOf("p_user_id" to userId)
        return SupabaseClient.client.postgrest.rpc("get_user_inbox", params)
            .decodeList<ConversationItem>()
    }
}