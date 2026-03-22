package com.example.hcmus_quickhelper.features.auth.datasource

import com.example.hcmus_quickhelper.core.database.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class AuthRemoteDataSource {
    suspend fun loginWithEmail(email: String, pass: String) {
        SupabaseClient.client.auth.signInWith(Email) {
            this.email = email
            this.password = pass
        }
    }
}
