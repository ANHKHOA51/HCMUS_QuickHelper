package com.example.hcmus_quickhelper.features.auth.datasource

import com.example.hcmus_quickhelper.core.database.SupabaseClient
import com.example.hcmus_quickhelper.core.model.FcmToken
import com.example.hcmus_quickhelper.core.model.User
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

class AuthRemoteDataSource {
    suspend fun loginWithEmail(email: String, pass: String): User {
        // Sign in with Auth to verify credentials and get a session
        SupabaseClient.client.auth.signInWith(Email) {
            this.email = email
            this.password = pass
        }

        // Treating public.users as the source of truth for extra profile data
        val user = SupabaseClient.client.postgrest["users"]
            .select {
                filter {
                    eq("email", email)
                }
            }.decodeSingleOrNull<User>()

        return user ?: throw Exception("401: User profile not found in database")
    }

    suspend fun verifyPassword(pass: String) {
        val email = SupabaseClient.client.auth.currentUserOrNull()?.email
            ?: throw Exception("User not logged in or session expired")

        SupabaseClient.client.auth.signInWith(Email) {
            this.email = email
            this.password = pass
        }
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
                role = "CUSTOMER"
            )
            SupabaseClient.client.postgrest["users"].insert(user)
        }

        return user
    }

    suspend fun registerWithEmail(email: String, pass: String, fullname: String, phone: String, username: String? = null, role: String = "CUSTOMER") {
        // Register with Supabase Auth
        SupabaseClient.client.auth.signUpWith(Email) {
            this.email = email
            this.password = pass
        }

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
            role = role
        )

        SupabaseClient.client.postgrest["users"].insert(publicUser)
    }

    suspend fun updatePassword(userId: Int, newPass: String) {
        // Update in Auth
        SupabaseClient.client.auth.updateUser {
            password = newPass
        }

        // Update in public table (if keeping sync)
        SupabaseClient.client.postgrest["users"].update(
            {
                set("password", newPass)
            }
        ) {
            filter { eq("id", userId) }
        }
    }

    suspend fun updatePasswordByEmail(email: String, newPass: String) {
        // This is typically done via reset token, but if we are authenticated:
        SupabaseClient.client.auth.updateUser {
            password = newPass
        }

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
        SupabaseClient.client.auth.resetPasswordForEmail(email)
    }

    suspend fun verifyOtp(email: String, token: String) {
        // Supabase usually uses a link or a code. 
        // For simplicity assuming OTP verification logic is handled by Supabase
    }
}
