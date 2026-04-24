package com.example.hcmus_quickhelper.features.admin_statistic.repository

import com.example.hcmus_quickhelper.features.admin_statistic.datasource.StatisticDataSource

class StatisticRepository(
    private val dataSource: StatisticDataSource = StatisticDataSource()
) {
    suspend fun getUsers() = dataSource.getUsers()
    suspend fun getServices() = dataSource.getServices()
    suspend fun getPayments() = dataSource.getPayments()
    suspend fun getBookings() = dataSource.getBookings()
}