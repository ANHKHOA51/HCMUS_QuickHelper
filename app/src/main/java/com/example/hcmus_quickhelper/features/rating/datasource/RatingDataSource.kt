package com.example.hcmus_quickhelper.features.rating.datasource

import com.example.hcmus_quickhelper.core.database.SupabaseClient
import com.example.hcmus_quickhelper.features.payment.model.Payment
import com.example.hcmus_quickhelper.features.rating.model.Rating
import io.github.jan.supabase.postgrest.from

class RatingDataSource {

    suspend fun insert(rating: Rating): Rating {
        return SupabaseClient.client.from("ratings").insert(rating) {
            select()
        }.decodeSingle<Rating>()
    }
}