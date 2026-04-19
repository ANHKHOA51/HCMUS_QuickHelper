package com.example.hcmus_quickhelper.features.auth.datasource

import com.example.hcmus_quickhelper.core.database.SupabaseClient
import com.example.hcmus_quickhelper.core.model.User
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

class AuthRemoteDataSource {
    suspend fun loginWithEmail(email: String, pass: String): User {
        // Treating public.users as the source of truth for custom auth
        val user = SupabaseClient.client.postgrest["users"]
            .select {
                filter {
                    eq("email", email)
                    eq("password", pass)
                }
            }.decodeSingleOrNull<User>()

        return user ?: throw Exception("401: Invalid email or password")
    }

    suspend fun registerWithEmail(email: String, pass: String, fullname: String, phone: String) {
        // 1. Fetch the highest ID currently in the table
        val highestUser = SupabaseClient.client.postgrest["users"]
            .select {
                order("id", order = Order.DESCENDING)
                limit(1)
            }.decodeSingleOrNull<User>()

        val nextId = (highestUser?.id ?: 0) + 1

        // 2. Create the User object with the new ID
        val publicUser = User(
            id = nextId,
            fullname = fullname,
            email = email,
            phone = phone,
            password = pass,
            role = "user"
        )

        // 3. Insert into public.users
        SupabaseClient.client.postgrest["users"].insert(publicUser)
    }

    suspend fun sendOtp(email: String) {
        // OTP is no longer used in custom database auth
    }

    suspend fun verifyOtp(email: String, token: String) {
        // OTP is no longer used in custom database auth
    }
}
