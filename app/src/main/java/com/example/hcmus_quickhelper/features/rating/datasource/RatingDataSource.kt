package com.example.hcmus_quickhelper.features.rating.datasource

import com.example.hcmus_quickhelper.core.database.SupabaseClient
import com.example.hcmus_quickhelper.features.payment.model.Payment
import com.example.hcmus_quickhelper.features.rating.model.Rating
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class RatingDataSource {

    suspend fun insert(rating: Rating): Rating {
        return SupabaseClient.client.from("ratings").insert(rating) {
            select()
        }.decodeSingle<Rating>()
    }

    suspend fun getByHelperId(helperId: Int): List<Rating> {
        return SupabaseClient.client.from("ratings").select(
            columns = Columns.raw("*, bookings!inner(helper_id)")
        ) {
            filter {
                eq("bookings.helper_id", helperId)
            }
        }.decodeList<Rating>()
    }

    suspend fun updateRating(rating: Double, helperId: Int) {
        SupabaseClient.client.from("users").update(
            {
                set("rating", rating)
            }
        ) {
            filter {
                eq("id", helperId)
            }
        }
    }
}