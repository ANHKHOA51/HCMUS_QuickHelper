package com.example.hcmus_quickhelper.features.chat.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun subscribeMessages(conversationId: Int) {
        subscribeJob?.cancel()

        subscribeJob = viewModelScope.launch {
            repository.subscribeMessages(conversationId)
                .collect { newMsg ->

                    val current = messageList.value ?: emptyList()

                    val exists = current.any { it.messageId == newMsg.messageId }
                    if (exists) return@collect

                    messageList.value = current + newMsg
                }
        }
    }

    fun sendMessage(conversationId: Int, senderId: Int, content: String) {
        viewModelScope.launch {

            val result = repository.sendMessage(conversationId, senderId, content)

            result.onSuccess { newMessage ->

                val current = messageList.value?.toMutableList() ?: mutableListOf()

                current.add(newMessage)

                messageList.value = current
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        subscribeJob?.cancel()
    }
}