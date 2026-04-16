package com.example.hcmus_quickhelper.features.booking.datasource

import com.example.hcmus_quickhelper.core.database.SupabaseClient
import com.example.hcmus_quickhelper.core.model.Booking
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.query.filter.FilterOperator

class BookingDataSource {
    suspend fun getAll(): List<Booking> {
        return SupabaseClient.client.from("bookings").select().decodeList<Booking>()
    }

    // Query helper, customer, service data
    suspend fun getAllFullData(): List<Booking> {
        return SupabaseClient.client.from("bookings").select(
            columns = Columns.raw("""
            *,
            customer:customer_id(*),
            helper:helper_id(*),
            services(*)
        """.trimIndent())
        ).decodeList<Booking>()
    }

    suspend fun getAllByHelperIdFullData(helperId: Int): List<Booking> {
        return SupabaseClient.client.from("bookings").select(
            columns = Columns.raw("""
            *,
            customer:customer_id(*),
            helper:helper_id(*),
            services(*)
        """.trimIndent())
        ) {
            filter {
                or {
                    eq("helper_id", helperId)
                    filter("helper_id",FilterOperator.IS, "null")
                }
            }
            order("created_at", Order.DESCENDING)
        }.decodeList<Booking>()
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

    suspend fun getByIdFullData(id: Int): Booking {
        return SupabaseClient.client.from("bookings").select(
            columns = Columns.raw("""
            *,
            customer:customer_id(*),
            helper:helper_id(*),
            services(*)
        """.trimIndent())
        ) {
            filter {
                eq("id", id)
            }
        }.decodeSingle<Booking>()
    }
}