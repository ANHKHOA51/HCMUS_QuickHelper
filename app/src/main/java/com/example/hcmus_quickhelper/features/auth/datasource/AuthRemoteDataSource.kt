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
        SupabaseClient.client.auth.signInWith(IDToken) { 
            this.idToken = idToken
            this.provider = Google
        }

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
                password = "", 
                role = "user"
            )
            SupabaseClient.client.postgrest["users"].insert(user)
        }

        return user
    }

    suspend fun registerWithEmail(email: String, pass: String, fullname: String, phone: String, username: String? = null) {
        val highestUser = SupabaseClient.client.postgrest["users"]
            .select {
                order("id", order = Order.DESCENDING)
                limit(1)
            }.decodeSingleOrNull<User>()

        val nextId = (highestUser?.id ?: 0) + 1

        val publicUser = User(
            id = nextId,
            fullname = fullname,
            username = username,
            email = email,
            phone = phone,
            password = pass,
            role = UserRole.CUSTOMER.toString()
        )

        SupabaseClient.client.postgrest["users"].insert(publicUser)
    }

    suspend fun updatePassword(userId: Int, newPass: String) {
        SupabaseClient.client.postgrest["users"].update(
            {
                set("password", newPass)
            }
        ) {
            filter { eq("id", userId) }
        }
    }

    suspend fun updatePasswordByEmail(email: String, newPass: String) {
        SupabaseClient.client.postgrest["users"].update(
            {
                set("password", newPass)
            }
        ) {
            filter { eq("email", email) }
        }
    }

    suspend fun saveFcmToken(userId: Int, token: String) {
        val fcmToken = FcmToken(userId, token)
        SupabaseClient.client.postgrest["fcm_tokens"].upsert(fcmToken)
    }

    suspend fun sendOtp(email: String) {
        // No-op
    }

    suspend fun verifyOtp(email: String, token: String) {
        // No-op
    }
}
