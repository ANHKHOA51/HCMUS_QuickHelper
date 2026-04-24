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
                set("phone", user.phone)
            }
        ) {
            filter { eq("id", user.id) }
            select()
        }.decodeSingle<User>()
    }
}
