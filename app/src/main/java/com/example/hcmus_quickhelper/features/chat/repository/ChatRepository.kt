package com.example.hcmus_quickhelper.features.chat.repository

import com.example.hcmus_quickhelper.features.chat.datasource.ChatRemoteDataSource
import com.example.hcmus_quickhelper.features.chat.model.ConversationItem
import com.example.hcmus_quickhelper.features.chat.model.Message
import kotlinx.coroutines.flow.Flow

class ChatRepository(
    private val dataSource: ChatRemoteDataSource
) {
    suspend fun getConversations(userId: Int): Result<List<ConversationItem>> {
        return try {
            val result = dataSource.getChatList(userId)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMessages(conversationId: Int): Result<List<Message>> {
        return try {
            val result = dataSource.getMessages(conversationId)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendMessage(
        conversationId: Int,
        senderId: Int,
        content: String
    ): Result<Message> {
        return try {
            val message = dataSource.sendMessage(conversationId, senderId, content)
            Result.success(message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markMessagesAsRead(
        conversationId: Int,
        currentUserId: Int
    ): Result<Unit> {

        return try {

            dataSource.markMessagesAsRead(
                conversationId,
                currentUserId
            )

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}