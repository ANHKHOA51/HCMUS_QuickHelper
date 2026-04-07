package com.example.hcmus_quickhelper.features.community.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.features.community.model.FeedDetail
import com.example.hcmus_quickhelper.features.community.repository.CommunityRepository
import kotlinx.coroutines.launch

class FeedDetailViewModel (
    private val repository: CommunityRepository
) : ViewModel() {
    val feedContent = MutableLiveData<FeedDetail>()
    val commentList = MutableLiveData<List<FeedDetail>>()

    fun fetchFeedDetail(feedId: Int, userId: Int) {
        viewModelScope.launch {
            val result = repository.getFeedDetail(feedId, userId)

            result.onSuccess { list ->
                if (list.isNotEmpty()) {
                    feedContent.value = list.first()

                    commentList.value = list
                }
            }
        }
    }
}