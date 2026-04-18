package com.example.hcmus_quickhelper.features.voucher.repository

import com.example.hcmus_quickhelper.features.voucher.datasource.VoucherDataSource
import com.example.hcmus_quickhelper.features.voucher.model.CollectVoucherResponse
import com.example.hcmus_quickhelper.features.voucher.model.Voucher

class VoucherRepository (
    private val voucherDataSource: VoucherDataSource
) {
    suspend fun getAllVoucherGlobal(): List<Voucher> {
        return voucherDataSource.getAll()
    }

    suspend fun getAllVoucherCollectible(userId: Int): List<Voucher> {
        return voucherDataSource.getCollectible(userId)
    }

    suspend fun getVouchersByUserId(userId: Int): List<Voucher> {
        return voucherDataSource.getByOwner(userId)
    }

    suspend fun getVoucherById(id: Int): Voucher? {
        return voucherDataSource.getById(id)
    }

    suspend fun collectVoucher(voucherId: Int, userId: Int): CollectVoucherResponse {
        return voucherDataSource.collectVoucher(voucherId, userId)
    }
}