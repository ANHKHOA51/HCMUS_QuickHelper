package com.example.hcmus_quickhelper.features.voucher.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Voucher(
    val id: String,
    val name: String,
    val description: String,
    val code: String,
    val quantity: Int,
    val discount: Double,
    val minPrice: Int,
    val expiredAt: String
) : Parcelable