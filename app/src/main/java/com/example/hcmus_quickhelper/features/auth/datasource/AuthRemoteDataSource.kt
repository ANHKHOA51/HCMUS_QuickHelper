package com.example.hcmus_quickhelper.features.auth.datasource

import com.example.hcmus_quickhelper.core.database.SupabaseClient
import com.example.hcmus_quickhelper.core.model.FcmToken
import com.example.hcmus_quickhelper.core.model.User
import com.example.hcmus_quickhelper.core.model.UserRole
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
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

    suspend fun loginWithGoogle(idToken: String): User {
        // This now correctly resolves because dependencies are no longer conflicting
        SupabaseClient.client.auth.signInWith(IDToken) { 
            this.idToken = idToken
            this.provider = Google
        }

        // 2. After successful auth, fetch or create the user in our public.users table
        val session = SupabaseClient.client.auth.currentSessionOrNull()
            ?: throw Exception("Failed to retrieve Supabase session after Google sign-in")

        val email = session.user?.email ?: throw Exception("No email found in Google session")

        var user = SupabaseClient.client.postgrest["users"]
            .select {
                filter {
                    eq("email", email)
                }
            }.decodeSingleOrNull<User>()

        if (user == null) {
            // Auto-register the Google user in our custom table if they don't exist
            val highestUser = SupabaseClient.client.postgrest["users"]
                .select {
                    order("id", order = Order.DESCENDING)
                    limit(1)
                }.decodeSingleOrNull<User>()

            val nextId = (highestUser?.id ?: 0) + 1
            user = User(
                id = nextId,
                fullname = session.user?.userMetadata?.get("full_name")?.toString() ?: "Google User",
                email = email,
                phone = "",
                password = "", // No password for Google users
                role = "user"
            )
            SupabaseClient.client.postgrest["users"].insert(user)
        }

        return user
    }

    suspend fun registerWithEmail(email: String, pass: String, fullname: String, phone: String, username: String? = null) {
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
            username = username,
            email = email,
            phone = phone,
            password = pass,
            role = UserRole.CUSTOMER.toString()
        )

        // 3. Insert into public.users
        SupabaseClient.client.postgrest["users"].insert(publicUser)
    }

    suspend fun saveFcmToken(userId: Int, token: String) {
        val fcmToken = FcmToken(userId, token)
        SupabaseClient.client.postgrest["fcm_tokens"].upsert(fcmToken)
    }

    suspend fun sendOtp(email: String) {
        // No-op: Handled by Android Intent in UI layer
    }

    suspend fun verifyOtp(email: String, token: String) {
        // No-op: Handled locally in UI layer
    }
}
