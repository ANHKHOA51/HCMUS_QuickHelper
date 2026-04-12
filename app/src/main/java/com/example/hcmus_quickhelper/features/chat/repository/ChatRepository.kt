package com.example.hcmus_quickhelper.features.chat.repository

import com.example.hcmus_quickhelper.features.chat.datasource.ChatRemoteDataSource
import com.example.hcmus_quickhelper.features.chat.model.ConversationItem
import com.example.hcmus_quickhelper.features.chat.model.Message
import kotlinx.coroutines.flow.Flow

class ChatRepository(
    private val dataSource: ChatRemoteDataSource
) {
    suspend fun getConservations(userId: Int): Result<List<ConversationItem>> {
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
    ): Result<Unit> {
        return try {
            dataSource.sendMessage(conversationId, senderId, content)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun subscribeMessages(conversationId: Int): Flow<Message> {
        return dataSource.subscribeMessages(conversationId)
    }

    fun subscribeAllMessages(): Flow<Message> {
        return dataSource.subscribeAllMessages()
    }
}