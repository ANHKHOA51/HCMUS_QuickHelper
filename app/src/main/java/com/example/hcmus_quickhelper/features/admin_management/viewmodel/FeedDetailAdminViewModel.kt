package com.example.hcmus_quickhelper.features.admin_management.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.core.model.User
import com.example.hcmus_quickhelper.features.admin_management.repository.ManagementRepository
import com.example.hcmus_quickhelper.features.community.model.CommentUI
import com.example.hcmus_quickhelper.features.community.model.Feed
import com.example.hcmus_quickhelper.features.community.model.FeedDetail
import com.example.hcmus_quickhelper.features.community.repository.CommunityRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class FeedDetailAdminViewModel (
    private val repository: ManagementRepository,
) : ViewModel() {
    val feedContent = MutableLiveData<FeedDetail>()
    val commentList = MutableLiveData<MutableList<CommentUI>>()

    fun fetchFeedDetail(feedId: Int?) {
        if (feedContent.value != null) return

        viewModelScope.launch {
            val result = repository.getFeedDetail(feedId!!)

            result.onSuccess { list ->
                if (list.isNotEmpty()) {
                    val feed = list.first()

                    val comments = list.mapNotNull { item ->
                        item.commentContent?.let { content ->
                            CommentUI(
                                commentContent = content,
                                commentorName = item.commentorName,
                                commentorAvt = item.commentorAvt,
                                commentTime = item.commentTime
                            )
                        }
                    }.toMutableList()

                    feedContent.value = feed
                    commentList.value = comments
                }
            }
        }
    }

    fun updateFeed(updated: FeedDetail) {
        feedContent.value = updated
    }

}