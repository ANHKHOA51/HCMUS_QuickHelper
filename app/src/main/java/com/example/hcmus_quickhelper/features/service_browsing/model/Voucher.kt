package com.example.hcmus_quickhelper.features.service_browsing.model

data class Voucher(
    val id: String,
    val code: String,
    val title: String, // expired and discount
    val colorHex: String
)