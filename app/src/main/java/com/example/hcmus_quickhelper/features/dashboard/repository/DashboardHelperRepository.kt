package com.example.hcmus_quickhelper.features.dashboard.repository

import com.example.hcmus_quickhelper.features.dashboard.datasource.DashboardHelperDataSource
import com.example.hcmus_quickhelper.features.dashboard.model.DashboardHelper
import com.example.hcmus_quickhelper.features.payment.model.Payment

class DashboardHelperRepository (
    private val dataSource: DashboardHelperDataSource
) {
    suspend fun getDashboardClientData(helperId: Int) : DashboardHelper {
        return dataSource.getByHelperFullData(helperId)
    }

    suspend fun getPayments(): List<Payment> {
        return dataSource.getPayments()
    }
}