package com.example.hcmus_quickhelper.features.service_browsing.repository

import com.example.hcmus_quickhelper.core.model.User
import com.example.hcmus_quickhelper.features.service_browsing.datasource.HomeRemoteDataSource
import com.example.hcmus_quickhelper.features.service_browsing.model.Helper
import com.example.hcmus_quickhelper.features.service_browsing.model.ServiceDto
import com.example.hcmus_quickhelper.features.service_browsing.model.Voucher

class HomeRepository(private val remoteDataSource: HomeRemoteDataSource) {
    suspend fun getUserProfile(userId: Int): Result<User> = runCatching { remoteDataSource.getUserProfile(userId) }
    suspend fun getVouchers(userId: Int): Result<List<Voucher>> = runCatching { remoteDataSource.getVouchers(userId) }
    suspend fun getPopularServices(): Result<List<ServiceDto>> = runCatching { remoteDataSource.getPopularServices() }
    suspend fun getTopHelpers(): Result<List<Helper>> = runCatching { remoteDataSource.getTopHelpers() }
}