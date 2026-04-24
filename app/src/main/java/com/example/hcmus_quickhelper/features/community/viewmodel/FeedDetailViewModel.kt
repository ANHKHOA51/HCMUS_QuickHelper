package com.example.hcmus_quickhelper.features.community.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.core.model.User
import com.example.hcmus_quickhelper.features.community.model.CommentUI
import com.example.hcmus_quickhelper.features.community.model.Feed
import com.example.hcmus_quickhelper.features.community.model.FeedDetail
import com.example.hcmus_quickhelper.features.community.repository.CommunityRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class FeedDetailViewModel (
    private val repository: CommunityRepository,
    private val currentUser: User?
) : ViewModel() {
    val feedContent = MutableLiveData<FeedDetail>()
    val commentList = MutableLiveData<MutableList<CommentUI>>()

    fun fetchFeedDetail(feedId: Int?, userId: Int) {
        if (feedContent.value != null) return

        viewModelScope.launch {
            val result = repository.getFeedDetail(feedId!!, userId)

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

    fun postComment(feedId: Int, userId: Int, content: String) {
        viewModelScope.launch {
            val result = repository.postComment(feedId, userId, content)

            result.onSuccess { comment ->
                val newComment = CommentUI(
                    commentContent = comment.content,
                    commentorName = currentUser?.fullname,
                    commentorAvt = currentUser?.avatarUrl,
                    commentTime = LocalDateTime.now().toString()
                )

                val currentList = commentList.value ?: mutableListOf()

                currentList.add(0, newComment)

                commentList.value = currentList
            }
        }
    }

    fun updateFeed(updated: FeedDetail) {
        feedContent.value = updated
    }

    fun toggleLike(feed: FeedDetail?, userId: Int) {
        if (userId == -1) return
        if(feed == null) return

        val isCurrentlyLiked = feed.isLiked
        val newLikeCount = if (isCurrentlyLiked) feed.likeCount - 1 else feed.likeCount + 1
        val updatedFeed = feed.copy(isLiked = !isCurrentlyLiked, likeCount = newLikeCount)

        updateFeed(updatedFeed)

        viewModelScope.launch {
            try {
                val (serverLiked, serverCount) = repository.toggleLike(feed.feedId, userId)
                if (serverLiked != updatedFeed.isLiked || serverCount != updatedFeed.likeCount) {
                    updateFeed(updatedFeed.copy(isLiked = serverLiked, likeCount = serverCount))
                }
            } catch (e: Exception) {
                updateFeed(feed)
            }
        }
    }

}