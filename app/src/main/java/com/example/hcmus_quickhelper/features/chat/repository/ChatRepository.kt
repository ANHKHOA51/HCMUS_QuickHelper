package com.example.hcmus_quickhelper.features.chat.repository

import com.example.hcmus_quickhelper.features.chat.datasource.ChatRemoteDataSource
import com.example.hcmus_quickhelper.features.chat.model.ConversationItem

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
}