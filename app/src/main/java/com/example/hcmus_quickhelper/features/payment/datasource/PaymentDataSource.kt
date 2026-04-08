package com.example.hcmus_quickhelper.features.payment.datasource

import android.util.Log
import com.example.hcmus_quickhelper.core.database.SupabaseClient
import com.example.hcmus_quickhelper.features.payment.model.Payment
import io.github.jan.supabase.postgrest.from

class PaymentDataSource {
    suspend fun getById(id: Int): Payment {
        return SupabaseClient.client.from("payments")
            .select {
                filter { eq("id", id) }
            }
            .decodeSingle<Payment>()
    }

    suspend fun getAll(): List<Payment> {
        Log.d("TEST", "TEST")
        return SupabaseClient.client.from("payments").select().decodeList<Payment>()
    }
}