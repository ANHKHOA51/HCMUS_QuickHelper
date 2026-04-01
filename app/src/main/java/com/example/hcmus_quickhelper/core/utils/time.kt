package com.example.hcmus_quickhelper.core.utils

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