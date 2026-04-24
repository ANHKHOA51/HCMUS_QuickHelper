package com.example.hcmus_quickhelper.core.service

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.hcmus_quickhelper.core.database.SupabaseClient
import io.github.jan.supabase.storage.storage
import java.util.UUID

object StorageService {
    suspend fun uploadImage(imageUri: Uri, context: Context, bucketName: String = "image_storage"): String {
        return try {
            val contentResolver = context.contentResolver
            val bytes = contentResolver.openInputStream(imageUri)?.use { it.readBytes() }
                ?: throw Exception("Không thể đọc dữ liệu từ Uri: $imageUri")

            val bucket = SupabaseClient.client.storage.from(bucketName)
            val fileName = "${UUID.randomUUID()}.jpg"

            bucket.upload(fileName, bytes) {
                upsert = true
            }

            val publicUrl = bucket.publicUrl(fileName)
            publicUrl
        } catch (e: Exception) {
            Log.e("StorageService", "Lỗi khi upload ảnh: ${e.message}", e)
            ""
        }
    }

    suspend fun deleteImage(fileName: String, bucketName: String = "image_storage"): Boolean {
        return try {
            val bucket = SupabaseClient.client.storage.from(bucketName)
            bucket.delete(fileName)
            Log.d("StorageService", "Xóa ảnh thành công: $fileName")
            true
        } catch (e: Exception) {
            Log.e("StorageService", "Lỗi khi xóa ảnh: ${e.message}", e)
            false
        }
    }
}
