package com.example.hcmus_quickhelper.features.admin_management.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
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
                                commentId = item.commentId,
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

    suspend fun deleteFeed(feedId: Int): Result<Unit> {
        return repository.deleteFeed(feedId)
    }

    fun deleteComment(commentId: Int)  {
        viewModelScope.launch {
            val result = repository.deleteComment(commentId)

            result.onSuccess {
                val currentList = commentList.value ?: mutableListOf()
                val newList = currentList.filterNot { it.commentId == commentId }
                commentList.value = newList.toMutableList()
            }
        }

    }

}