package com.example.hcmus_quickhelper.features.booking.datasource

import com.example.hcmus_quickhelper.features.booking.model.BookingRequest
import com.example.hcmus_quickhelper.features.booking.model.BookingStatus

class MockBookingRequestDataSource {
    val bookingRequests = listOf(
        BookingRequest(
            id = 1,
            schedule = "2024-05-20 09:00",
            address = "123 Đường Lê Lợi, Quận 1, TP.HCM",
            customerName = "Nguyễn Văn A",
            customerPhone = "0901234567",
            customerAvatar = "https://i.pravatar.cc/150?u=1",
            customerRating = 4.8,
            serviceName = "Dọn dẹp nhà cửa",
            status = BookingStatus.PENDING.toString(),
            totalPrice = 250000.0,
            createdAt = "2024-05-19 14:30"
        ),
        BookingRequest(
            id = 2,
            schedule = "2024-05-20 14:00",
            address = "456 Nguyễn Thị Minh Khai, Quận 3, TP.HCM",
            customerName = "Trần Thị B",
            customerPhone = "0912345678",
            customerAvatar = "https://i.pravatar.cc/150?u=2",
            customerRating = 5.0,
            serviceName = "Giặt ủi cao cấp",
            status = BookingStatus.CONFIRMED.toString(),
            totalPrice = 150000.0,
            createdAt = "2024-05-19 16:00"
        ),
        BookingRequest(
            id = 3,
            schedule = "2024-05-21 08:30",
            address = "789 Điện Biên Phủ, Bình Thạnh, TP.HCM",
            customerName = "Lê Văn C",
            customerPhone = "0923456789",
            customerAvatar = "https://i.pravatar.cc/150?u=3",
            customerRating = 4.5,
            serviceName = "Sửa chữa điện nước",
            status = BookingStatus.IN_PROGRESS.toString(),
            totalPrice = 500000.0,
            createdAt = "2024-05-20 10:00"
        ),
        BookingRequest(
            id = 4,
            schedule = "2024-05-21 15:00",
            address = "12 Song Hành, Quận 2, TP.HCM",
            customerName = "Phạm Minh D",
            customerPhone = "0934567890",
            customerAvatar = "https://i.pravatar.cc/150?u=4",
            customerRating = 4.2,
            serviceName = "Vệ sinh máy lạnh",
            status = BookingStatus.COMPLETED.toString(),
            totalPrice = 350000.0,
            createdAt = "2024-05-20 11:20"
        ),
        BookingRequest(
            id = 5,
            schedule = "2024-05-22 10:00",
            address = "101 Tôn Dật Tiên, Quận 7, TP.HCM",
            customerName = "Hoàng Thị E",
            customerPhone = "0945678901",
            customerAvatar = "https://i.pravatar.cc/150?u=5",
            customerRating = 4.9,
            serviceName = "Trông trẻ theo giờ",
            status = BookingStatus.PENDING.toString(),
            totalPrice = 400000.0,
            createdAt = "2024-05-21 09:00"
        ),
        BookingRequest(
            id = 6,
            schedule = "2024-05-22 13:30",
            address = "22 Lý Tự Trọng, Quận 1, TP.HCM",
            customerName = "Đỗ Hoàng F",
            customerPhone = "0956789012",
            customerAvatar = "https://i.pravatar.cc/150?u=6",
            customerRating = 4.7,
            serviceName = "Nấu ăn tại nhà",
            status = BookingStatus.CONFIRMED.toString(),
            totalPrice = 600000.0,
            createdAt = "2024-05-21 15:45"
        ),
        BookingRequest(
            id = 7,
            schedule = "2024-05-23 07:00",
            address = "55 Phan Xích Long, Phú Nhuận, TP.HCM",
            customerName = "Bùi Văn G",
            customerPhone = "0967890123",
            customerAvatar = "https://i.pravatar.cc/150?u=7",
            customerRating = 4.6,
            serviceName = "Cắt tỉa cây cảnh",
            status = BookingStatus.IN_PROGRESS.toString(),
            totalPrice = 300000.0,
            createdAt = "2024-05-22 08:00"
        ),
        BookingRequest(
            id = 8,
            schedule = "2024-05-23 16:00",
            address = "88 Cộng Hòa, Tân Bình, TP.HCM",
            customerName = "Ngô Thị H",
            customerPhone = "0978901234",
            customerAvatar = "https://i.pravatar.cc/150?u=8",
            customerRating = 4.3,
            serviceName = "Chăm sóc thú cưng",
            status = BookingStatus.COMPLETED.toString(),
            totalPrice = 200000.0,
            createdAt = "2024-05-22 10:30"
        ),
        BookingRequest(
            id = 9,
            schedule = "2024-05-24 11:00",
            address = "333 Võ Văn Ngân, Thủ Đức, TP.HCM",
            customerName = "Vũ Văn I",
            customerPhone = "0989012345",
            customerAvatar = "https://i.pravatar.cc/150?u=9",
            customerRating = 5.0,
            serviceName = "Khử khuẩn nhà cửa",
            status = BookingStatus.PENDING.toString(),
            totalPrice = 450000.0,
            createdAt = "2024-05-23 07:15"
        ),
        BookingRequest(
            id = 10,
            schedule = "2024-05-24 18:00",
            address = "99 Quang Trung, Gò Vấp, TP.HCM",
            customerName = "Lý Thị K",
            customerPhone = "0990123456",
            customerAvatar = "https://i.pravatar.cc/150?u=10",
            customerRating = 4.4,
            serviceName = "Gói quà nghệ thuật",
            status = BookingStatus.CONFIRMED.toString(),
            totalPrice = 120000.0,
            createdAt = "2024-05-23 14:00"
        )
    )

    fun getAll(): List<BookingRequest> {
        return bookingRequests
    }
}