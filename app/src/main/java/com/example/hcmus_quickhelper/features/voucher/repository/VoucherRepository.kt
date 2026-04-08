package com.example.hcmus_quickhelper.features.voucher.repository

import com.example.hcmus_quickhelper.features.voucher.datasource.VoucherDataSource
import com.example.hcmus_quickhelper.features.voucher.model.Voucher

class VoucherRepository (
    private val voucherDataSource: VoucherDataSource
) {
    suspend fun getAll(): List<Voucher> {
        return voucherDataSource.getAll()
    }
}