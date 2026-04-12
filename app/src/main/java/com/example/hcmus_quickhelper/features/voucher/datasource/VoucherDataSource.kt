package com.example.hcmus_quickhelper.features.voucher.datasource

import com.example.hcmus_quickhelper.core.database.SupabaseClient
import com.example.hcmus_quickhelper.features.voucher.model.Voucher
import io.github.jan.supabase.postgrest.from

class VoucherDataSource {
    suspend fun getAll(): List<Voucher> {
        return SupabaseClient.client.from("vouchers").select().decodeList<Voucher>()
    }

    suspend fun getById(id: Int): Voucher? {
        return SupabaseClient.client.from("vouchers").select {
            filter { eq("id", id) }
        }
            .decodeSingleOrNull<Voucher>()
    }
}