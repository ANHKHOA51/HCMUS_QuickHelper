package com.example.hcmus_quickhelper.core.database

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth
import com.example.hcmus_quickhelper.core.database.SupabaseConfig

object SupabaseClient {

    val client = createSupabaseClient(
        supabaseUrl = SupabaseConfig.SUPABASE_URL,
        supabaseKey = SupabaseConfig.SUPABASE_KEY
    ) {
        install(Postgrest)
        install(Auth)
    }
}