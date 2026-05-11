package com.example.hcmus_quickhelper.features.settings.repository

import com.example.hcmus_quickhelper.core.auth.UserPreferences
import com.example.hcmus_quickhelper.core.database.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val userPreferences: UserPreferences) {
    val language: Flow<String> = userPreferences.language
    val pushNotificationsEnabled: Flow<Boolean> = userPreferences.pushNotificationsEnabled
    val emailNotificationsEnabled: Flow<Boolean> = userPreferences.emailNotificationsEnabled

    suspend fun updateLanguage(language: String) {
        userPreferences.updateLanguage(language)
    }

    suspend fun setPushNotificationsEnabled(enabled: Boolean) {
        userPreferences.setPushNotificationsEnabled(enabled)
    }

    suspend fun setEmailNotificationsEnabled(enabled: Boolean) {
        userPreferences.setEmailNotificationsEnabled(enabled)
    }

    suspend fun deleteFcmToken(userId: Int) {
        SupabaseClient.client.postgrest["fcm_tokens"].delete {
            filter {
                eq("user_id", userId)
            }
        }
    }
}
