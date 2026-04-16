package com.example.hcmus_quickhelper.features.service_browsing.repository

import com.example.hcmus_quickhelper.features.service_browsing.datasource.ServiceListLocalDataSource
import com.example.hcmus_quickhelper.features.service_browsing.datasource.ServiceListRemoteDataSource
import com.example.hcmus_quickhelper.features.service_browsing.model.Helper

class ServiceListRepository (
    private val dataSource: ServiceListRemoteDataSource
){
    suspend fun getHelpers(): Result<List<Helper>> {
        return try {
            val result = dataSource.getHelpersFromSupabase()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}