package com.example.hcmus_quickhelper.features.community.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.features.community.repository.CommunityRepository
import kotlinx.coroutines.launch

class PostFeedViewModel (
    private val repository: CommunityRepository
) : ViewModel() {

    fun postFeed(userId: Int, content: String, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.postFeed(userId, content)
            onDone()
        }
        Log.d("POST", "Post success")
    }
}
