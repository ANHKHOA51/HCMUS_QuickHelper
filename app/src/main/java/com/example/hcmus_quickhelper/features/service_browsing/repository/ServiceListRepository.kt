package com.example.hcmus_quickhelper.features.service_browsing.repository

import com.example.hcmus_quickhelper.features.service_browsing.datasource.ServiceListLocalDataSource
import com.example.hcmus_quickhelper.features.service_browsing.model.Helper

class ServiceListRepository (
    private val localDataSource: ServiceListLocalDataSource
){
    suspend fun getHelpers(): List<Helper>{
        return localDataSource.getMockHelpers()
    }
}