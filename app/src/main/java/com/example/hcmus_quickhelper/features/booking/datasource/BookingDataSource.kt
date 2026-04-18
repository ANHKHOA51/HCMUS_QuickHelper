package com.example.hcmus_quickhelper.features.booking.datasource

import com.example.hcmus_quickhelper.core.database.SupabaseClient
import com.example.hcmus_quickhelper.core.model.Booking
import com.example.hcmus_quickhelper.features.booking.model.BookingInsert
import com.example.hcmus_quickhelper.features.service_browsing.model.HelperWithServicesDto
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

    suspend fun update(id: Int, booking: BookingInsert) {
        SupabaseClient.client.from("bookings").update(booking)
    }

    suspend fun insertBooking(booking: BookingInsert) {
        SupabaseClient.client.from("bookings").insert(booking)
    }

    suspend fun getHelperWithServices(helperId: Int): HelperWithServicesDto {
        val columns = Columns.raw("id, fullname, avatar_url, rating, services(id, name, base_price)")
        return SupabaseClient.client.from("users")
            .select(columns = columns) {
                filter { eq("id", helperId) }
            }.decodeSingle<HelperWithServicesDto>()
    }
}