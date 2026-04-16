package com.example.hcmus_quickhelper.features.service_browsing.datasource

import com.example.hcmus_quickhelper.core.database.SupabaseClient
import com.example.hcmus_quickhelper.features.service_browsing.model.Helper
import com.example.hcmus_quickhelper.features.service_browsing.model.HelperWithServicesDto
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns

class ServiceListRemoteDataSource {

    suspend fun getHelpersFromSupabase(): List<Helper> {
        val columns = Columns.raw("id, fullname, avatar_url, rating, services(id, name, base_price)")

        val helpersDto = SupabaseClient.client.postgrest["users"]
            .select(columns = columns) {
                filter { eq("role", "HELPER") }
            }.decodeList<HelperWithServicesDto>()

        return helpersDto.map { dto ->
            val skillsText = if (dto.services.isNotEmpty()) {
                dto.services.joinToString(" • ") { it.name }
            } else {
                "Chưa cập nhật dịch vụ"
            }

            val minPrice = dto.services.minOfOrNull { it.basePrice } ?: 0.0

            Helper(
                id = dto.id,
                name = dto.fullname,
                avatarUrl = dto.avatarUrl,
                isOnline = true, // Default theo yêu cầu
                rating = dto.rating ?: 5.0,
                skills = skillsText,
                price = minPrice
            )
        }
    }
}