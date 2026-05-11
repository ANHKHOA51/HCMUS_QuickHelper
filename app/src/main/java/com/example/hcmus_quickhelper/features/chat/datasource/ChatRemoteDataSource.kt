package com.example.hcmus_quickhelper.features.chat.datasource

import android.util.Log
import com.example.hcmus_quickhelper.core.database.SupabaseClient
import com.example.hcmus_quickhelper.features.chat.model.ConversationItem
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import com.example.hcmus_quickhelper.features.chat.model.Message
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.decodeRecord
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
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
        Log.d("REALTIME", "SEND ROOM = $conversationId")
        val message = SupabaseClient.client.postgrest
            .from("chat_messages")
            .insert(
                buildJsonObject {
                    put("conversation_id", conversationId)
                    put("sender_id", senderId)
                    put("message", content)
                }
            ) {
                select()
            }
            .decodeSingle<Message>()

        return message
    }

//    fun subscribeMessages(conversationId: Int): Flow<Message> = callbackFlow {
//        Log.d("REALTIME", "SUBSCRIBE ROOM = $conversationId")
//        val client = SupabaseClient.client
//        val channel = client.realtime.channel("chat-$conversationId")
//
//        val job = launch {
//            channel.postgresChangeFlow<PostgresAction.Insert>(
//                schema = "public"
//            ) {
//                table = "chat_messages"
//                filter("conversation_id", FilterOperator.EQ, conversationId)
//            }.collect { change ->
//
//                val message = change.decodeRecord<Message>()
//                trySend(message)
//            }
//        }
//
//
//        channel.subscribe()
//
//
//        awaitClose {
//            job.cancel()
//
////            launch {
////                client.realtime.removeChannel(channel)
////            }
//        }
//    }
//    fun subscribeAllMessages(): Flow<Message> = callbackFlow {
//
//        val client = SupabaseClient.client
//        val channel = client.realtime.channel("all-messages")
//
//        val job = launch {
//            channel.postgresChangeFlow<PostgresAction.Insert>(
//                schema = "public"
//            ) {
//                table = "chat_messages"
//            }.collect { change ->
//
//                val message = change.decodeRecord<Message>()
//                trySend(message)
//            }
//        }
//
//        channel.subscribe()
//
//
//        awaitClose {
//            job.cancel()
//
////            launch {
////                client.realtime.removeChannel(channel)
////            }
//        }
//    }
}