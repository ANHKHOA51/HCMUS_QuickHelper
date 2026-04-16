package com.example.hcmus_quickhelper.features.service_browsing.datasource

import com.example.hcmus_quickhelper.core.model.User
import com.example.hcmus_quickhelper.features.service_browsing.model.Helper
import com.example.hcmus_quickhelper.features.service_browsing.model.Voucher
import kotlinx.coroutines.delay

class HomeLocalDataSource {
    suspend fun  getUserProfile(): User{
        return User(
            id = 1,
            fullname = "Nguyen Khoa",
            username = "Khoa",
            email = "khoa@gmail.com",
            phone = "0123456789",
            password = "123456",
            role = "user"
        )
    }

    suspend fun getVouchers(): List<Voucher>{
        return listOf(
            Voucher("1", "Mã: QUICK50", "Giảm 50 ", "#F27B4D"),
            Voucher("2", "Mã: QUICK60", "Giảm 60 ", "#F27B4D")
        )
    }

    suspend fun getTopHelpers(): List<Helper> {
        delay(300)
        return listOf(
        )
    }
}