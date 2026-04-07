package com.example.hcmus_quickhelper.features.community.repository

import com.example.hcmus_quickhelper.features.community.datasource.CommunityRemoteDataSource
import com.example.hcmus_quickhelper.features.community.model.Feed


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

}