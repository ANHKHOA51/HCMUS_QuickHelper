package com.example.hcmus_quickhelper.features.profile.datasource

import com.example.hcmus_quickhelper.core.database.SupabaseClient
import com.example.hcmus_quickhelper.core.model.User
import io.github.jan.supabase.postgrest.postgrest

class ProfileRemoteDataSource {
    suspend fun updateProfile(user: User): User {
        return SupabaseClient.client.postgrest["users"].update(
            {
                set("username", user.username)
                set("fullname", user.fullname)
            }
        ) {
            filter { eq("id", user.id) }
            // Ensure we are explicitly asking for all columns to match the User model
            select()
        }.decodeSingle<User>()
    }
}
