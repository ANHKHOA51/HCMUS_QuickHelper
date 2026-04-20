package com.example.hcmus_quickhelper.features.chat.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.features.chat.model.ConversationItem
import com.example.hcmus_quickhelper.features.chat.repository.ChatRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ConversationViewModel(
    private val repository: ChatRepository
) : ViewModel() {

    val conversationList = MutableLiveData<List<ConversationItem>>(emptyList())

    private var subscribeJob: Job? = null

    fun fetchConversations(userId: Int) {
        Log.d("fetchConversations", "userId: $userId")
        if (userId == -1) return;
        viewModelScope.launch {
            val result = repository.getConservations(userId)

            result.onSuccess { list ->
                conversationList.value = list
            }
        }
    }

    fun subscribeAllMessages() {

        subscribeJob?.cancel()

        subscribeJob = viewModelScope.launch {
            repository.subscribeAllMessages()
                .collect { newMsg ->

                    val current = conversationList.value?.toMutableList() ?: mutableListOf()

                    val index = current.indexOfFirst {
                        it.conversationId == newMsg.conversationId
                    }

                    if (index == -1) return@collect

                    val oldItem = current[index]

                    val updatedItem = oldItem.copy(
                        latestMessage = newMsg.message,
                        lastMessageTime = newMsg.createdAt
                    )

                    // move lên đầu
                    current.removeAt(index)
                    current.add(0, updatedItem)

                    conversationList.value = current
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        subscribeJob?.cancel()
    }
}