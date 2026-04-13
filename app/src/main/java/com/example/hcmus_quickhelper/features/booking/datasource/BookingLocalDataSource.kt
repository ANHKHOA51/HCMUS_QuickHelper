package com.example.hcmus_quickhelper.features.booking.datasource

import com.example.hcmus_quickhelper.features.booking.model.BookingHistory

class BookingLocalDataSource {
    suspend fun getBookingHistories(): List<BookingHistory> {
        return listOf(
            BookingHistory(
                id = "1",
                serviceName = "Dọn dẹp nhà cửa",
                status = "ĐANG THỰC HIỆN",
                priceText = "350.000đ",
                packageType = "Gói cơ bản",
                date = "24 Th10, 2023",
                time = "09:00 AM"
            ),
            BookingHistory(
                id = "2",
                serviceName = "Sửa máy lạnh",
                status = "ĐÃ XÁC NHẬN",
                priceText = "480.000đ",
                packageType = "Bảo trì định kỳ",
                date = "26 Th10, 2023",
                time = "02:30 PM"
            ),
            BookingHistory(
                id = "3",
                serviceName = "Nấu ăn gia đình",
                status = "ĐÃ HOÀN THÀNH",
                priceText = "200.000đ",
                packageType = "Gói 2 giờ",
                date = "20 Th10, 2023",
                time = "10:00 AM"
            ),
            BookingHistory(
                id = "4",
                serviceName = "Sửa ống nước",
                status = "ĐÃ HỦY",
                priceText = "150.000đ",
                packageType = "Sửa chữa nhanh",
                date = "15 Th10, 2023",
                time = "03:00 PM"
            )
        )
    }
}