package com.example.hcmus_quickhelper.features.chat.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.features.chat.ChatRealtimeManager
import com.example.hcmus_quickhelper.features.chat.model.Message
import com.example.hcmus_quickhelper.features.chat.repository.ChatRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: ChatRepository
) : ViewModel() {
    val messageList = MutableLiveData<List<Message>>()

    private var subscribeJob: Job? = null

    fun fetchMessage(conversationId: Int) {
        viewModelScope.launch {
            val result = repository.getMessages(conversationId)

            result.onSuccess { list ->
                messageList.value = list
            }
        }
    }

    fun subscribeMessages(
        conversationId: Int
    ) {

        viewModelScope.launch {

            ChatRealtimeManager.messages
                .collect { message ->

                    if (
                        message.conversationId
                        == conversationId
                    ) {

                        val current =
                            messageList.value
                                ?.toMutableList()
                                ?: mutableListOf()

                        current.add(message)

                        messageList.value = current
                    }
                }
        }
    }

    fun sendMessage(
        conversationId: Int,
        senderId: Int,
        content: String
    ) {

        viewModelScope.launch {

            try {

                repository.sendMessage(
                    conversationId,
                    senderId,
                    content
                )

            } catch (e: Exception) {

                Log.e(
                    "SEND_MESSAGE",
                    e.stackTraceToString()
                )
            }
        }
    }

    fun markMessagesAsRead(
        conversationId: Int,
        currentUserId: Int
    ) {

        viewModelScope.launch {

            repository.markMessagesAsRead(
                conversationId,
                currentUserId
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        subscribeJob?.cancel()
    }
}