package com.example.hcmus_quickhelper.features.service_browsing.datasource

import com.example.hcmus_quickhelper.features.service_browsing.model.Helper

class ServiceListLocalDataSource {

    suspend fun getMockHelpers(): List<Helper> {

        return listOf(
            Helper(
                id = "1",
                name = "Nguyễn Thị Hoa",
                avatarUrl = "",
                isOnline = true,
                isVerified = true,
                rating = 4.9,
                reviewCount = 128,
                skills = "Dọn dẹp nhà • Nấu ăn",
                distance = 1.2,
                priceText = "80.000đ/giờ"
            ),
            Helper(
                id = "2",
                name = "Trần Văn Nam",
                avatarUrl = "",
                isOnline = false,
                isVerified = true,
                rating = 4.8,
                reviewCount = 95,
                skills = "Sửa điện • Ống nước",
                distance = 2.5,
                priceText = "100.000đ/giờ"
            ),
            Helper(
                id = "3",
                name = "Lê Thị Bích",
                avatarUrl = "",
                isOnline = true,
                isVerified = false,
                rating = 4.5,
                reviewCount = 42,
                skills = "Giặt ủi • Trông trẻ",
                distance = 3.1,
                priceText = "70.000đ/giờ"
            ),
            Helper(
                id = "4",
                name = "Phạm Minh Tuấn",
                avatarUrl = "",
                isOnline = true,
                isVerified = true,
                rating = 5.0,
                reviewCount = 210,
                skills = "Sửa điện lạnh • Cơ khí",
                distance = 0.8,
                priceText = "120.000đ/giờ"
            )
        )
    }
}