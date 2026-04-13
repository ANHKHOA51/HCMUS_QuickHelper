package com.example.hcmus_quickhelper.core.utils

import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.ZonedDateTime

fun String.toMessageTime(): String {
    return try {
        // 1. Đảm bảo chuỗi chuẩn UTC và parse thành Instant
        val validUtcString = if (this.endsWith("Z")) this else "${this}Z"
        val instant = Instant.parse(validUtcString)

        // 2. Chuyển từ UTC sang múi giờ hiện hành của thiết bị
        val target = instant.atZone(ZoneId.systemDefault())
        val now = ZonedDateTime.now()

        // 3. Kiểm tra ngày và format
        if (target.toLocalDate() == now.toLocalDate()) {
            // Nếu là hôm nay -> Chỉ hiện Giờ:Phút (VD: 14:30)
            target.format(DateTimeFormatter.ofPattern("HH:mm"))
        } else {
            // Nếu là ngày khác -> Hiện Ngày/Tháng Giờ:Phút (VD: 12/04 14:30)
            target.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))
        }
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

fun String.toSmartTime(): String {
    return try {
        val ldt = java.time.LocalDateTime.parse(this)
        val target = ldt.atZone(java.time.ZoneId.systemDefault())
        val now = java.time.ZonedDateTime.now()

        if (target.toLocalDate() == now.toLocalDate()) {
            target.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
        } else {
            target.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM"))
        }
    } catch (e: Exception) {
        ""
    }
}

fun String?.toRelativeTime(): String {
    if (this.isNullOrBlank()) return ""

    return try {
        val cleanInput = this.replace(" ", "T").let {
            if (!it.contains("Z") && !it.contains("+")) "${it}Z" else it
        }

        val pastInstant = Instant.parse(cleanInput)
        val nowInstant = Instant.now()
        val duration = Duration.between(pastInstant, nowInstant)

        val seconds = duration.seconds

        when {
            seconds < 60 -> "Vừa xong"

            seconds < 3600 -> {
                val minutes = seconds / 60
                "$minutes phút trước"
            }

            seconds < 86400 -> {
                val hours = seconds / 3600
                "$hours giờ trước"
            }

            else -> {
                // Trên 1 ngày thì hiện ngày/tháng theo múi giờ hệ thống
                val target = pastInstant.atZone(ZoneId.systemDefault())
                target.format(DateTimeFormatter.ofPattern("dd/MM"))
            }
        }
    } catch (e: Exception) {
        // Nếu lỗi parse, trả về 10 ký tự đầu (thường là YYYY-MM-DD)
        this?.take(10) ?: ""
    }
}
