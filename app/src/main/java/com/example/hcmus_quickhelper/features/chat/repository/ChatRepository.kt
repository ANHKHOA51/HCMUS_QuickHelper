package com.example.hcmus_quickhelper.features.chat.repository

import com.example.hcmus_quickhelper.features.chat.datasource.ChatRemoteDataSource
import com.example.hcmus_quickhelper.features.chat.model.ConversationItem
import com.example.hcmus_quickhelper.features.chat.model.Message

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
}