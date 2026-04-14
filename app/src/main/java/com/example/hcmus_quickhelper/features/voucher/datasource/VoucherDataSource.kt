package com.example.hcmus_quickhelper.features.voucher.datasource

import com.example.hcmus_quickhelper.core.database.SupabaseClient
import com.example.hcmus_quickhelper.features.voucher.model.CollectVoucherResponse
import com.example.hcmus_quickhelper.features.voucher.model.Voucher
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc

class VoucherDataSource {
    suspend fun getAll(): List<Voucher> {
        return SupabaseClient.client.from("vouchers").select().decodeList<Voucher>()
    }

    suspend fun getCollectible(userId: Int): List<Voucher> {
        return SupabaseClient.client.postgrest.rpc(
            "get_voucher_collectible",
            mapOf("p_user_id" to userId)
        ).decodeList<Voucher>()
    }

    suspend fun getById(id: Int): Voucher? {
        return SupabaseClient.client.from("vouchers").select {
            filter { eq("id", id) }
        }
            .decodeSingleOrNull<Voucher>()
    }

    suspend fun collectVoucher(voucherId: Int, userId: Int): CollectVoucherResponse {
        return SupabaseClient.client.postgrest.rpc(
            "collect_voucher",
            mapOf(
                "p_voucher_id" to voucherId,
                "p_user_id" to userId
            )
        ).decodeSingle<CollectVoucherResponse>()
    }
}