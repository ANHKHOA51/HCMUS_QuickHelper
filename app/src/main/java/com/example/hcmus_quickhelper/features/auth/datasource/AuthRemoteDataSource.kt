package com.example.hcmus_quickhelper.features.auth.datasource

import com.example.hcmus_quickhelper.core.database.SupabaseClient
import com.example.hcmus_quickhelper.core.model.FcmToken
import com.example.hcmus_quickhelper.core.model.User
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

class AuthRemoteDataSource {
    suspend fun login(identifier: String, pass: String): User {
        val user = SupabaseClient.client.postgrest["users"]
            .select {
                filter {
                    or {
                        eq("email", identifier)
                        eq("phone", identifier)
                    }
                }
            }.decodeSingleOrNull<User>()

        if (user != null && user.password == pass) {
            return user
        } else {
            throw Exception("401: Invalid email/phone or password")
        }
    }

    suspend fun getUserByEmail(email: String): User? {
        return SupabaseClient.client.postgrest["users"]
            .select {
                filter {
                    eq("email", email)
                }
            }.decodeSingleOrNull<User>()
    }

    suspend fun verifyPassword(email: String, pass: String): Boolean {
        val user = getUserByEmail(email)
        return user?.password == pass
    }

    suspend fun registerWithEmail(email: String, pass: String, fullname: String, phone: String, username: String? = null, role: String = "CUSTOMER") {
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
        // Implement manual OTP logic if needed
    }

    suspend fun verifyOtp(email: String, token: String) {
        // Implement manual OTP verification logic if needed
    }
}
