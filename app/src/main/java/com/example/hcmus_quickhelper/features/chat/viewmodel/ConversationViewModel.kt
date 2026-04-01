package com.example.hcmus_quickhelper.features.chat.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.features.chat.model.ConversationItem
import com.example.hcmus_quickhelper.features.chat.repository.ChatRepository
import kotlinx.coroutines.launch

class ConversationViewModel(
    private val repository: ChatRepository
) : ViewModel() {
    val conversationList = MutableLiveData<List<ConversationItem>>()

    fun fetchConversations(userId: Int) {
        viewModelScope.launch {
            val result = repository.getConservations(userId)

            result.onSuccess { list ->
                conversationList.value = list
            }
        }
    }
}
