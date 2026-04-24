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
        if (userId == -1) return

        viewModelScope.launch {
            val result = repository.getFeeds(userId)

            result.onSuccess { newList ->
                feedList.value = newList
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

    fun updateFeed(updated: Feed) {
        feedList.value = feedList.value?.map {
            if (it.id == updated.id) updated else it
        }
    }
    fun toggleLike(feed: Feed, userId: Int) {
        if (userId == -1) return

        val isCurrentlyLiked = feed.isLiked
        val newLikeCount = if (isCurrentlyLiked) feed.likeCount - 1 else feed.likeCount + 1
        val updatedFeed = feed.copy(isLiked = !isCurrentlyLiked, likeCount = newLikeCount)

        updateFeed(updatedFeed)

        viewModelScope.launch {
            try {
                val (serverLiked, serverCount) = repository.toggleLike(feed.id, userId)
                if (serverLiked != updatedFeed.isLiked || serverCount != updatedFeed.likeCount) {
                    updateFeed(updatedFeed.copy(isLiked = serverLiked, likeCount = serverCount))
                }
            } catch (e: Exception) {
                updateFeed(feed)
            }
        }
    }

}