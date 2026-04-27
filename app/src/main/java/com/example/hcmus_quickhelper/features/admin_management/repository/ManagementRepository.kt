package com.example.hcmus_quickhelper.features.admin_management.repository

import android.util.Log
import com.example.hcmus_quickhelper.core.model.User
import com.example.hcmus_quickhelper.features.admin_management.datasource.ManagementDataSource
import com.example.hcmus_quickhelper.features.community.model.Comment
import com.example.hcmus_quickhelper.features.community.model.Feed
import com.example.hcmus_quickhelper.features.community.model.FeedDetail

class ManagementRepository (
    private val dataSource: ManagementDataSource
) {
    suspend fun getFeeds(): Result<List<Feed>> {
        return try {
            val result = dataSource.getFeeds()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUsers(): Result<List<User>> {
        return try {
            val result = dataSource.getUsers()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFeedDetail(feedId: Int): Result<List<FeedDetail>> {
        return try {
            val result = dataSource.getFeedDetail(feedId)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}