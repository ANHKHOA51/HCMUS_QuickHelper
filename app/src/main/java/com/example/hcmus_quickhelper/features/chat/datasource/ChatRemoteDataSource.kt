package com.example.hcmus_quickhelper.features.chat.datasource

import com.example.hcmus_quickhelper.core.database.SupabaseClient
import com.example.hcmus_quickhelper.features.chat.model.ConversationItem
import com.example.hcmus_quickhelper.features.chat.model.Message
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class ChatRemoteDataSource {
    suspend fun getChatList(userId: Int): List<ConversationItem> {
        val params = mapOf("p_user_id" to userId)
        return SupabaseClient.client.postgrest.rpc("get_user_inbox", params)
            .decodeList<ConversationItem>()
    }

    suspend fun getMessages(conversationId: Int): List<Message> {
        return SupabaseClient.client.postgrest.from("chat_messages")
            .select {
                filter {
                    eq("conversation_id", conversationId)
                }
                order("created_at", order = Order.ASCENDING)
            }
            .decodeList<Message>()
    }

    suspend fun sendMessage(
        conversationId: Int,
        senderId: Int,
        content: String
    ): Message {
        val message = SupabaseClient.client.postgrest
            .from("chat_messages")
            .insert(
                buildJsonObject {
                    put("conversation_id", conversationId)
                    put("sender_id", senderId)
                    put("message", content)
                    put("is_read", false)
                }
            ) {
                select()
            }
            .decodeSingle<Message>()

        return message
    }

    suspend fun markMessagesAsRead(
        conversationId: Int,
        currentUserId: Int
    ) {

        SupabaseClient.client.postgrest
            .from("chat_messages")
            .update(
                {
                    set("is_read", true)
                }
            ) {
                filter {
                    eq("conversation_id", conversationId)

                    neq("sender_id", currentUserId)

                    eq("is_read", false)
                }
            }
    }
}