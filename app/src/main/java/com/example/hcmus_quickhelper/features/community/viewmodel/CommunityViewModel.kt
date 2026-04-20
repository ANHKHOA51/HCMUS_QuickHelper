package com.example.hcmus_quickhelper.features.community.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.features.community.model.Feed
import com.example.hcmus_quickhelper.features.community.repository.CommunityRepository
import kotlinx.coroutines.launch

class CommunityViewModel (
    private val repository: CommunityRepository
) : ViewModel() {
    val feedList = MutableLiveData<List<Feed>>()

    fun fetchFeeds(userId: Int) {
        if (userId == -1) return;
        viewModelScope.launch {
            val result = repository.getFeeds(userId)

            result.onSuccess { list ->
                feedList.value = list
            }
        }
    }

    fun fetchPopularFeeds(userId: Int) {
        viewModelScope.launch {
            val result = repository.getPopularFeeds(userId)

            result.onSuccess { list ->
                feedList.value = list
            }
        }
    }
}