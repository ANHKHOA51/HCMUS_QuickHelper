package com.example.hcmus_quickhelper.features.dashboard.datasource

import com.example.hcmus_quickhelper.core.database.SupabaseClient
import com.example.hcmus_quickhelper.features.dashboard.model.DashboardHelper
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class DashboardHelperDataSource {

    suspend fun getByHelperFullData(helperId: Int): DashboardHelper {
        return SupabaseClient.client.from("users")
            .select(
                columns = Columns.raw("""
            rating,
            bookings!bookings_helper_id_fkey(
                *,
                services(*),
                customer:users!bookings_customer_id_fkey(*)
            )
            """.trimIndent())
            ) {
                filter {
                    eq("id", helperId)
                }
            }
            .decodeSingle<DashboardHelper>()
    }
}