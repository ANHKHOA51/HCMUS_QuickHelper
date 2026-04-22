package com.example.hcmus_quickhelper.features.community.repository

import android.util.Log
import com.example.hcmus_quickhelper.features.community.datasource.CommunityRemoteDataSource
import com.example.hcmus_quickhelper.features.community.model.Comment
import com.example.hcmus_quickhelper.features.community.model.Feed
import com.example.hcmus_quickhelper.features.community.model.FeedDetail


class CommunityRepository(
    private val dataSource: CommunityRemoteDataSource
) {
    suspend fun getFeeds(userId: Int): Result<List<Feed>> {
        return try {
            val result = dataSource.getFeeds(userId)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getPopularFeeds(userId: Int): Result<List<Feed>> {
        return try {
            val result = dataSource.getPopularFeeds(userId)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFeedDetail(feedId: Int, userId: Int): Result<List<FeedDetail>> {
        return try {
            val result = dataSource.getFeedDetail(feedId, userId)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun postComment(feedId: Int, userId: Int, content: String): Result<Comment> {
        return try {
            val comment = dataSource.postComment(feedId, userId, content)
            Result.success(comment)
        } catch (e: Exception) {
            Log.e("CommunityRepository", "Error posting comment: ${e.message}")
            Result.failure(e)
        }
    }
}