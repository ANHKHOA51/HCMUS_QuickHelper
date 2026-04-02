package com.example.hcmus_quickhelper.features.voucher.datasource

import com.example.hcmus_quickhelper.features.voucher.model.Voucher

class MockVoucherDataSource {
    suspend fun getAll(): List<Voucher> {
        return listOf(
            Voucher(
                id = "1",
                name = "Chào Bạn Mới",
                description = "Giảm 20% cho khách hàng lần đầu đặt món tại quán.",
                code = "CHAOMUNG2026",
                quantity = 100,
                discount = 0.2,
                minPrice = 50000,
                expiredAt = "2026-12-31T23:59:59Z"
            ),
            Voucher(
                id = "2",
                name = "Cà Phê Sáng",
                description = "Tặng ngay 15k khi đặt cà phê trong khung giờ 6h - 9h sáng.",
                code = "CAPHESANG",
                quantity = 50,
                discount = 15000.0,
                minPrice = 30000,
                expiredAt = "2026-06-01T10:00:00Z"
            ),
            Voucher(
                id = "3",
                name = "Miễn Phí Giao Hàng",
                description = "Giảm tối đa 20k phí vận chuyển cho mọi đơn hàng.",
                code = "FREESHIP",
                quantity = 200,
                discount = 20000.0,
                minPrice = 0,
                expiredAt = "2026-05-20T23:59:59Z"
            ),
            // Voucher này dành cho các dịp cuối tuần
            Voucher(
                id = "4",
                name = "Cuối Tuần Vui Vẻ",
                description = "Giảm 10% khi đi nhóm từ 3 người trở lên vào Thứ 7 và CN.",
                code = "CUOITUAN",
                quantity = 30,
                discount = 0.1,
                minPrice = 100000,
                expiredAt = "2026-04-15T23:59:59Z"
            ),
            Voucher(
                id = "5",
                name = "Ưu Đãi Thành Viên VIP",
                description = "Giảm nửa giá cho thành viên hạng Kim Cương.",
                code = "VIPONLY",
                quantity = 10,
                discount = 0.5,
                minPrice = 200000,
                expiredAt = "2026-12-31T23:59:59Z"
            )
        )
    }
}