package com.example.hcmus_quickhelper.features.service_browsing.datasource

import android.app.Service
import com.example.hcmus_quickhelper.core.database.SupabaseClient
import com.example.hcmus_quickhelper.core.model.User
import com.example.hcmus_quickhelper.features.service_browsing.model.Helper
import com.example.hcmus_quickhelper.features.service_browsing.model.HelperWithServicesDto
import com.example.hcmus_quickhelper.features.service_browsing.model.ServiceDto
import com.example.hcmus_quickhelper.features.service_browsing.model.Voucher
import com.example.hcmus_quickhelper.features.service_browsing.model.VoucherOwnerDto
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns

class HomeRemoteDataSource {

    suspend fun getUserProfile(userId: Int): User {
        return SupabaseClient.client.postgrest["users"]
            .select { filter { eq("id", userId) } }
            .decodeSingle<User>()
    }

    suspend fun getVouchers(userId: Int): List<Voucher> {
        val columns = Columns.raw("*, vouchers(*)")
        val dtos = SupabaseClient.client.postgrest["voucher_owners"]
            .select(columns = columns) {
                filter { eq("owner_id", userId) }
            }.decodeList<VoucherOwnerDto>()

        return dtos.mapNotNull {
            val v = it.vouchers ?: return@mapNotNull null
            Voucher(
                id = v.id.toString(),
                code = "Mã: ${v.code}",
                title = "Giảm ${v.discount.toInt()}đ",
                colorHex = "#F27B4D" // Custom màu
            )
        }
    }

    suspend fun getPopularServices(): List<ServiceDto> {
        return SupabaseClient.client.postgrest["services"]
            .select().decodeList<ServiceDto>()
    }

    suspend fun getTopHelpers(): List<Helper> {
        val columns = Columns.raw("id, fullname, avatar_url, rating, services(id, name, base_price)")
        val dtos = SupabaseClient.client.postgrest["users"]
            .select(columns = columns) {
                filter {
                    ilike("role", "helper")
                    gte("rating", 4.5)
                }
            }.decodeList<HelperWithServicesDto>()

        return dtos.map { dto ->
            val skillsText = if (dto.services.isNotEmpty()) {
                dto.services.joinToString(" • ") { it.name }
            } else "Đa dịch vụ"

            val minPrice = dto.services.minOfOrNull { it.basePrice } ?: 0.0

            Helper(
                id = dto.id,
                name = dto.fullname,
                avatarUrl = dto.avatarUrl,
                isOnline = true,
                rating = dto.rating ?: 5.0,
                skills = skillsText,
                price = minPrice
            )
        }
    }
}