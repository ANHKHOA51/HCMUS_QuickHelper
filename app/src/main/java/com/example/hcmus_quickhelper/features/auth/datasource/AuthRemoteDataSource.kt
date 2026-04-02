package com.example.hcmus_quickhelper.features.auth.datasource

import com.example.hcmus_quickhelper.core.database.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRemoteDataSource {
    suspend fun loginWithEmail(email: String, pass: String) {
        SupabaseClient.client.auth.signInWith(Email) {
            this.email = email
            this.password = pass
        }
    }

    suspend fun registerWithEmail(email: String, pass: String, fullname: String, phone: String) {
        SupabaseClient.client.auth.signUpWith(Email) {
            this.email = email
            this.password = pass
            data = buildJsonObject {
                put("full_name", fullname)
                put("phone", phone)
            }
        }
    }
}
