package com.example.hcmus_quickhelper.core.utils

import java.text.NumberFormat
import java.util.Locale

object MoneyUtils {
    fun formatVietnameseCurrency(amount: Double): String {
        return try {
            val localeVN = Locale("vi", "VN")
            val formatter = NumberFormat.getCurrencyInstance(localeVN)
            formatter.format(amount)
        } catch (e: Exception) {
            "$amount"
        }
    }
}