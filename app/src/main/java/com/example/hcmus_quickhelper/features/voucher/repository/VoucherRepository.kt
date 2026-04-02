package com.example.hcmus_quickhelper.features.voucher.repository

import com.example.hcmus_quickhelper.features.voucher.datasource.MockVoucherDataSource
import com.example.hcmus_quickhelper.features.voucher.model.Voucher

class VoucherRepository (
    private val voucherDataSource: MockVoucherDataSource
) {
    suspend fun getAll(): List<Voucher> {
        return voucherDataSource.getAll()
    }
}