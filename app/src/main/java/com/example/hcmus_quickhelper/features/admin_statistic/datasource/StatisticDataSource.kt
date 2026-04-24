package com.example.hcmus_quickhelper.features.admin_statistic.datasource

import com.example.hcmus_quickhelper.core.database.SupabaseClient
import com.example.hcmus_quickhelper.features.admin_statistic.model.StatUser
import io.github.jan.supabase.postgrest.from
import com.example.hcmus_quickhelper.features.admin_statistic.model.StatBooking
import com.example.hcmus_quickhelper.features.admin_statistic.model.StatPayment
import com.example.hcmus_quickhelper.features.admin_statistic.model.StatService
import io.github.jan.supabase.postgrest.query.Columns

class StatisticDataSource {
    suspend fun getUsers(): List<StatUser> {
        return SupabaseClient.client.from("users")
            .select(columns = Columns.raw("id, role, fullname, rating"))
            .decodeList<StatUser>()
    }

    suspend fun getServices(): List<StatService> {
        return SupabaseClient.client.from("services")
            .select(columns = Columns.raw("id"))
            .decodeList<StatService>()
    }

    suspend fun getPayments(): List<StatPayment> {
        return SupabaseClient.client.from("payments")
            .select(columns = Columns.raw("id, amount, booking_id, created_at"))
            .decodeList<StatPayment>()
    }

    suspend fun getBookings(): List<StatBooking> {
        return SupabaseClient.client.from("bookings")
            .select(columns = Columns.raw("id, helper_id"))
            .decodeList<StatBooking>()
    }
}