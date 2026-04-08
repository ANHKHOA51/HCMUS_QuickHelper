package com.example.hcmus_quickhelper.features.booking.datasource

import com.example.hcmus_quickhelper.core.database.SupabaseClient
import com.example.hcmus_quickhelper.core.model.Booking
import io.github.jan.supabase.postgrest.from

class BookingDataSource {
    suspend fun getAll(): List<Booking> {
        return SupabaseClient.client.from("bookings").select().decodeList<Booking>()
    }

    suspend fun getById(id: Int): Booking {
        return SupabaseClient.client.from("bookings")
            .select {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingle<Booking>()
    }
}