package com.example.hcmus_quickhelper.features.service_browsing.repository

import com.example.hcmus_quickhelper.features.service_browsing.datasource.HomeLocalDataSource

class HomeRepository(private val localDataSource: HomeLocalDataSource) {
    suspend fun getUserProfile() = localDataSource.getUserProfile()
    suspend fun getVouchers() = localDataSource.getVouchers()
    suspend fun getTopHelpers() = localDataSource.getTopHelpers()
}