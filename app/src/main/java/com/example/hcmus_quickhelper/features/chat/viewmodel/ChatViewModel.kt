package com.example.hcmus_quickhelper.features.chat.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.features.chat.model.ConversationItem
import com.example.hcmus_quickhelper.features.chat.model.Message
import com.example.hcmus_quickhelper.features.chat.repository.ChatRepository
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: ChatRepository
) : ViewModel() {
    val messageList = MutableLiveData<List<Message>>()

    fun fetchMessage(conversationId: Int) {
        viewModelScope.launch {
            val result = repository.getMessages(conversationId)

            result.onSuccess { list ->
                messageList.value = list
            }
        }
    }
}